
# 📱CryptoMonitor - Monitor de Cotação do Bitcoin

Bem-vindo ao CryptoMonitor!
Este é um aplicativo Android simples, desenvolvido em **Kotlin**, que permite acompanhar em tempo real o valor atual do Bitcoin (BTC), utilizando a API pública do **Mercado Bitcoin.**


[![Captura-de-tela-2025-04-26-232559.png](https://i.postimg.cc/gcQ2gjJr/Captura-de-tela-2025-04-26-232559.png)](https://postimg.cc/grypJY3P)

[![Captura-de-tela-2025-04-27-180130.png](https://i.postimg.cc/9XYV7Yn4/Captura-de-tela-2025-04-27-180130.png)](https://postimg.cc/dD3zMrvv)

## 📦 Estrutura do Projeto

```bash
app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/
│   └── riqinho.com.github.cryptomonitor/
│       ├── MainActivity.kt
│       ├── model/
│       │   └── TickerResponse.kt
│       ├── service/
│       │   ├── MercadoBitcoinService.kt
│       │   └── MercadoBitcoinServiceFactory.kt
│       └── ui.theme/ (estilos visuais)
├── res/
│   ├── layout/ (layouts XML)
│   └── values/ (strings, cores, temas)

````
### 🛡️ AndroidManifest.xml
Arquivo .xml que descreve para o Sistema Android tudo que o app é, o que ele faz e do que ele precisa para funcionar corretamente. No nosso projeto, por exemplo, é aqui onde adicionamos a permissão para se conectar com a internet.

### 🎯 MainActivity.kt
É o controle principal da aplicação, o responsável por "orquestrar"o funcionamento do nosso app. Este arquivo:
- Configura a tela inicial, carregando o layout activity_main.xml, que por sua vez, inclui a Toolbar, textos e botão de atualização
- Possui a função que realiza a chamada assíncrona (graças à biblioteca kotlinx-coroutines-android), responsável por buscar dados da API e ataulizar dinamicamente o conteúdo da página
- Faz o tratamento de erros, exibindo mensagemnsse caso algo não funcione como esperado

```bash
package riqinho.com.github.cryptomonitor

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import riqinho.com.github.cryptomonitor.service.MercadoBitcoinServiceFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //configurando toolbar
        val toolbarMain: Toolbar = findViewById(R.id.toolbar_main)
        configureToolbar(toolbarMain)

        //configurando o botão refresh
        val btnRefresh: Button = findViewById(R.id.btn_refresh)
        btnRefresh.setOnClickListener {
            makeRestCall()
        }
    }

    private fun configureToolbar(toolbar: Toolbar){
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(getColor(R.color.white))
        supportActionBar?.setTitle(getText(R.string.app_title))

        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.primary))
    }

    private fun makeRestCall(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val service = MercadoBitcoinServiceFactory().create()
                val response = service.getTicker()

                if(response.isSuccessful){
                    val tickerResponse = response.body()

                    //atualizando os componentes TextView
                    val lblValue: TextView = findViewById(R.id.lbl_value)
                    val lblDate: TextView = findViewById(R.id.lbl_date)

                    val lastValue = tickerResponse?.ticker?.last?.toDoubleOrNull()
                    if(lastValue != null){
                        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                        lblValue.text = numberFormat.format(lastValue)
                    }

                    val date = tickerResponse?.ticker?.date?.let { Date(it * 1000L) }
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    lblDate.text = sdf.format(date)

                } else {
                    // Trate o erro de resposta não bem-sucedida
                    val errorMessage = when (response.code()) {
                        400 -> "Bad Request"
                        401 -> "Unauthorized"
                        403 -> "Forbidden"
                        404 -> "Not Found"
                        else -> "Unknown error"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Trate o erro de falha na chamada
                Toast.makeText(this@MainActivity, "Falha na chamada: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
```

#### O que acontece aqui?
- No onCreate()
  - Carregamos o layout da dela activity_main.xml, com o setContentView()
  - Chamamos configureToolbar(), onde definimos ela como a ActionBar principal, a cor do texto como branco, o título e a cor de fundo
  - Pegamos o botão de atualizar e adicionamos um listener (setOnClickListener) para executar a função makeRestCall(), quando ele for clicado
  
- Dentro da função makeRestCall():
  - Criamos uma Coroutine no Dispatchers.Main, para que a chamada à API aconteça de forma assíncrona.
  - Criamos uma instância do Retrofit usando MercadoBitcoinServiceFactory.
  - Fazemos a chamada getTicker() para buscar a cotação do Bitcoin.
  - Se a resposta for bem-sucedida (response.isSuccessful):
    - Pegamos o objeto tickerResponse da resposta.
    - Atualizamos o componente lblValue (que mostra o valor do Bitcoin) com o último preço (last), convertendo o valor de String para Double e formatando para o padrão de moeda brasileira usando NumberFormat.
    - Atualizamos também o componente lblDate (que mostra a última atualização), convertendo a data (date) para um objeto Date e formatando a data para o padrão dd/MM/yyyy HH:mm:ss usando SimpleDateFormat.
  - Se a resposta não for bem-sucedida:
    - Verificamos o código de erro HTTP (400, 401, 403, 404...) e mostramos uma mensagem apropriada para o usuário através de um Toast.
    - Se ocorrer uma exceção, mostramos uma mensagem de erro geral para o usuário usando um Toast


### 📄 Model/TickerResponse.kt
Arquivo Kotlin onde fica a classe responsável por representar os dados recebidos da API.
Normalmente, as informações retornadas por uma API vêm no formato JSON.
O que essa classe faz, resumidamente, é facilitar o acesso a esses dados:
quando o Retrofit (biblioteca que adicinamos no build.gradle) baixa a resposta da API, ele automaticamente transforma o JSON em um objeto TickerResponse.
Assim, podemos acessar os dados diretamente no código, sem precisar manipular o JSON manualmente.
```bash
package riqinho.com.github.cryptomonitor.model

class TickerResponse(
    val ticker: Ticker
)

class Ticker(
    val high: String,
    val low: String,
    val vol: String,
    val last: String,
    val buy: String,
    val sell: String,
    val date: Long
)
```
#### O que acontece aqui?
- TickerResponse representa o objeto inteiro que a API devolve
- Dentro dele, temos o atributo ticker, do tipo Ticker, que bate exatamente com a chave "ticker" no JSON retornado pela API. Chamamos isso de contrato
- A classe Ticker é responsável por guardar os valores reais da cotação: preço máximo, preço mínimo, volume, último valor... todos os campos que existem no JSON da API.

### 🌐 Service/MercadoBitcoinService.kt
Arquivo de interface responsável por definir o serviço da API que o app utiliza para buscar a cotação do Bitcoin.
No nosso caso, fazemos requisições GET para o endpoint "api/BTC/ticker".
Quando a chamada é concluída, recebemos um objeto TickerResponse, que por sua vez, contém os dados da cotação do Bitcoin.

```bash
package riqinho.com.github.cryptomonitor.service

import retrofit2.Response
import retrofit2.http.GET
import riqinho.com.github.cryptomonitor.model.TickerResponse

interface MercadoBitcoinService {
    @GET("api/BTC/ticker/")
    suspend fun getTicker(): Response<TickerResponse>
}
```

#### O que acontece aqui?
- A anotação @GET("api/BTC/ticker/") diz que o método getTicker() vai fazer uma requisição GET para esse caminho do servidor
- getTicker() é uma função suspend, o que define que ela será chamada de forma assíncrona usando coroutines
- Retorna um objeto TickerResponse, o que criamos para modelar a resposta da API

### 🏭 Service/MercadoBitcoinServiceFactory.kt
Arquivo responsável por criar a configuração do Retrofit e entregar um objeto MercadoBitcoinService para o app utilizar.
É aqui que definimos o endereço base da API (https://www.mercadobitcoin.net/) e também qual conversor de JSON será utilizado (no nosso caso, o Gson).

```bash
package riqinho.com.github.cryptomonitor.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MercadoBitcoinServiceFactory {

    fun create(): MercadoBitcoinService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.mercadobitcoin.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MercadoBitcoinService::class.java)
    }
}
```

#### O que acontece aqui?
- Usamos o Retro.build():
  - baseUrl -> definir o enderço principal da API
  - addConverterFactory() -> dizer que as respostas JSON serão convertidas automatizamente usando Gson
  - build() -> para montar o objeto Retrofit
- No final, cria e retorna uma instância de MercadoBitcoinService, a interface que faz a chamada real para a API

### 🎨 res/
Pastinha responsável por guardar os recursos visuais e de configuração que o app usa, como as telas, os textos, cores e imagens. Aqui, temos:
- layout/ -> guarda os arquivos de layout .xml
- values/ -> guarda valores reutilizávies, como: textos, cores, temas...
- drawable -> guarda formas (shapes) ou imagens

---

## 🛠️ Teste você mesmo!

- Clone o Projeto

  ```bash
  git clone https://github.com/riqinho/android-crypto-monitor.git
  ```
- Abra no Android Studio.
- Faça o Sync do Gradle.
- Execute o app em um emulador ou dispositivo físico com Android 
- Se divirta


