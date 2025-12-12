# Projeto: Sistema de Agricultura Inteligente com RMI e API REST

## Visão Geral

Este projeto simula um sistema de monitoramento de agricultura inteligente, focado em uma arquitetura de **Comunicação Direta (Ponto a Ponto)** utilizando **Java RMI (Remote Method Invocation)** e uma camada de consulta **HTTP/JSON (Javalin)**.

O objetivo é demonstrar uma solução distribuída onde o desacoplamento é menor, mas que oferece comunicação síncrona e imediata entre os componentes.

---

## Arquitetura do Projeto

O sistema é dividido em três módulos principais (Projetos Maven), que se comunicam através do RMI Registry.

### 1. Módulo Comum (`rmi-commons`)

Contém as interfaces e modelos de dados necessários para a comunicação RMI. Este módulo garante que todos os participantes falem a mesma "linguagem".

| Diretório    | Descrição da Responsabilidade                                                                                       |
| :----------- | :------------------------------------------------------------------------------------------------------------------ |
| `interfaces` | Contém os contratos RMI (interfaces) que definem os métodos remotos: `SensorInput`, `SensorQuery`, `AlertsControl`. |
| `model`      | Contém as classes de dados serializáveis (`SensorData`, `AvgData`, `Alert`) trocadas entre os módulos.              |

### 2. Sensor Aggregation Hub (`rmi-server`)

Este módulo atua como o Hub Central de Agregação e Distribuição de Dados no sistema. Ele centraliza a lógica de negócios e o armazenamento do estado do sistema.

| Diretório    | Descrição da Responsabilidade                                                                                                                      |
| :----------- | :------------------------------------------------------------------------------------------------------------------------------------------------- |
| `interfaces` | Implementa os serviços RMI (`SensorInputImpl`, `SensorQueryImpl`).                                                                                 |
| `model`      | Contém o `SensorDatabase`, que armazena _buffers_ e médias (`AvgData`) em memória.                                                                 |
| `http`       | Implementa o `HttpServerInitializer` (Javalin), que expõe os dados consolidados (`AvgData`) via API REST/JSON na porta **4567** para o _frontend_. |
| `App.java`   | Inicia o RMI Registry (porta 3000) e o Servidor HTTP.                                                                                              |

**Fluxo de Dados:**

1. Recebe leituras dos sensores via **RMI**.
2. Calcula a média dos últimos 5 registros.
3. Dispara notificações críticas para o Serviço de Alerta via **RMI Callback**.
4. Responde às consultas do _frontend_ via **HTTP/JSON**.

### 3. Serviço de Alertas (`rmi-alerts`)

Este é o componente que consome as notificações e as expõe ao _frontend_.

| Diretório        | Descrição da Responsabilidade                                                                                                             |
| :--------------- | :---------------------------------------------------------------------------------------------------------------------------------------- |
| **`interfaces`** | Contém a implementação da interface RMI (`AlertsControl`) que permite ao Sensor Aggregation Hub disparar as notificações de forma remota. |
| `services`       | Implementa o serviço `AlertsServiceImpl` (o RMI Callback), que recebe as notificações e as registra em um histórico.                      |
| `http`           | Implementa o `AlertsHttpInitializer` (Javalin), que expõe o histórico de alertas (`Alert.java`) via API REST/JSON na porta **4568**.      |
| `App.java`       | Inicia o servidor RMI de Alertas e o Servidor HTTP.                                                                                       |

--- |

### 4. Clientes Sensores (`rmi-agrosense`)

Este módulo simula o comportamento dos sensores, enviando dados para o Sensor Aggregation Hub.

- **Função Principal:** Cria e executa instâncias de sensores, chamando o método remoto (`sendData`) do Servidor CAT a cada 30 segundos.

| Diretório     | Descrição da Responsabilidade                                                                                                                                |
| :------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`client`**  | Contém a classe principal (`SensorClient`) que realiza a conexão com o RMI Registry, localiza o _stub_ do Servidor CAT e chama o método remoto (`sendData`). |
| **`sensors`** | Contém a lógica de simulação para gerar leituras de dados realistas (classes como `TemperatureSensor`, `PhSensor`, etc.).                                    |
| `App.java`    | O ponto de entrada que inicializa as simulações e começa o loop de envio de dados a cada 30 segundos.                                                        |

**Função Principal:** Cria e executa instâncias de sensores, chamando o método remoto (`sendData`) do Servidor CAT a cada 60 segundos.

---

## Ferramentas e Tecnologias

| Categoria         | Ferramenta            | Uso                                                                            |
| :---------------- | :-------------------- | :----------------------------------------------------------------------------- |
| **Linguagem**     | Java JDK 21.1         | Linguagem principal para todos os módulos distribuídos.                        |
| **Comunicação**   | Java RMI              | Comunicação Ponto a Ponto síncrona entre o CAT e Alertas/Sensores.             |
| **API REST**      | Javalin 5.x           | Micro-framework para expor as médias e alertas em HTTP/JSON para o _frontend_. |
| **Gerenciamento** | Apache Maven 3.9.11   | Ferramenta de _build_ e gerenciamento de dependências.                         |
| **Serialização**  | Jackson (via Javalin) | Usado para serializar objetos Java (`AvgData`, `Alert`) em formato **JSON**.   |

---

## Execução do Projeto

A execução deve seguir a ordem de dependência para garantir que os _services_ necessários estejam no RMI Registry:

1.  **Serviço de Alertas:** Execute o `AlertsApp.java` (inicia RMI e HTTP 4568).
2.  **Servidor CAT:** Execute o `ServerApp.java` (inicia RMI Registry 3000, RMI Services e HTTP 4567).
3.  **Clientes Sensores:** Execute o `App.java` (simula sensores enviando dados).

O _frontend_ pode então consultar o Servidor CAT (`:4567`) para os dados de medição e o Servidor de Alertas (`:4568`) para o histórico de notificações.

## Equipe

Luís Guilherme, Letícia Maria, Maria Eduarda, Maria Luiza e João Pedro Jacomé. Todos fazem parte do curso de bacharelado de ciência da computação, pelo IFCE Campus Tianguá.
