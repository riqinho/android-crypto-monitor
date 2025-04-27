
# 📱CryptoMonitor - Monitor de Cotação do Bitcoin

Bem-vindo ao CryptoMonitor!
Este é um aplicativo Android simples, desenvolvido em **Kotlin**, que permite acompanhar em tempo real o valor atual do Bitcoin (BTC), utilizando a API pública do **Mercado Bitcoin.**


[![Captura-de-tela-2025-04-26-232559.png](https://i.postimg.cc/gcQ2gjJr/Captura-de-tela-2025-04-26-232559.png)](https://postimg.cc/grypJY3P)

[![Captura-de-tela-2025-04-26-232559.png](https://i.postimg.cc/gcQ2gjJr/Captura-de-tela-2025-04-26-232559.png)](https://postimg.cc/grypJY3P)

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

### 📄 Model/TickerResponse.kt
Arquivo Kotlin onde fica a classe responsável por representar os dados recebidos da API.
Normalmente, as informações retornadas por uma API vêm no formato JSON.
O que essa classe faz, resumidamente, é facilitar o acesso a esses dados:
quando o Retrofit (biblioteca que adicinamos no build.gradle) baixa a resposta da API, ele automaticamente transforma o JSON em um objeto TickerResponse.
Assim, podemos acessar os dados diretamente no código, sem precisar manipular o JSON manualmente.

### 🌐 Service/MercadoBitcoinService.kt
Arquivo de interface responsável por definir o serviço da API que o app utiliza para buscar a cotação do Bitcoin.
No nosso caso, fazemos requisições GET para o endpoint "api/BTC/ticker".
Quando a chamada é concluída, recebemos um objeto TickerResponse, que por sua vez, contém os dados da cotação do Bitcoin.

### 🏭 Service/MercadoBitcoinServiceFactory.kt
Arquivo responsável por criar a configuração do Retrofit e entregar um objeto MercadoBitcoinService para o app utilizar.
É aqui que definimos o endereço base da API (https://www.mercadobitcoin.net/) e também qual conversor de JSON será utilizado (no nosso caso, o Gson).

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


