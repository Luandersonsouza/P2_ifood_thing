# Relatório do Projeto MyFood

## 1. Visão Geral da Arquitetura

O sistema **MyFood** é um delivery de alimentos, medicamentos e produtos de mercado, desenvolvido em Java com uma arquitetura em três camadas principais:

- **Modelos (models):** Entidades de negócio (usuários, empresas, produtos, pedidos, entregas). São classes POJO que implementam `Serializable` para permitir a persistência.
- **Serviços (services):** Contêm a lógica de negócio, validações e manipulação dos dados. Cada serviço (`UsuarioService`, `EmpresaService`, `ProdutoService`, `PedidoService`, `EntregaService`) é responsável por uma parte específica do sistema.
- **Fachada (Facade):** A classe `Facade` fornece uma interface única para todos os comandos da linguagem de script, exposta ao executor de testes. Ela delega as operações para os serviços adequados.
- **Persistência:** A classe `Database` (Singleton) mantém todos os dados em memória e os persiste automaticamente em arquivo XML (`myfood_data.xml`), utilizando `XMLEncoder`/`XMLDecoder`.

O projeto foi estruturado para atender aos testes de aceitação originais (User Stories 1 a 8) e passou com sucesso em todos os oito arquivos de script.

---

## 2. Estrutura de Diretórios

raiz/
├── lib/ # EasyAccept.jar (não incluso)
├── bin/ # Classes compiladas
├── MyFood/
│ ├── exceptions/
│ │ └── MyFoodException.java # Exceção de regra de negócio
│ ├── models/
│ │ ├── Usuario.java # Abstrata, base para usuários
│ │ ├── Cliente.java
│ │ ├── DonoEmpresa.java
│ │ ├── Entregador.java
│ │ ├── Empresa.java # Abstrata, base para empresas
│ │ ├── Restaurante.java
│ │ ├── Mercado.java
│ │ ├── Farmacia.java
│ │ ├── Produto.java
│ │ ├── Pedido.java
│ │ ├── Entrega.java
│ │ └── EstadoPedido.java # Enum com estados do pedido
│ ├── services/
│ │ ├── Database.java # Singleton de persistência
│ │ ├── Validador.java # Métodos estáticos de validação
│ │ ├── UsuarioService.java
│ │ ├── EmpresaService.java
│ │ ├── ProdutoService.java
│ │ ├── PedidoService.java
│ │ └── EntregaService.java
│ ├── Facade.java # Fachada principal
│ └── Main.java # Demonstração simples
├── tests/
│ ├── TestRunner.java # Executor próprio de scripts
│ ├── us*.txt # Scripts de aceitação
│ └── README.md
├── relatorio/
│ └── README.md # Este arquivo
└── README.md # Raiz do projeto


---

## 3. Principais Componentes e suas Responsabilidades

### 3.1 Modelos

- **Usuario (abstrata):** `id`, `nome`, `email`, `senha`, `endereco`. Subclasses: Cliente, DonoEmpresa (com CPF) e Entregador (veículo e placa).
- **Empresa (abstrata):** `id`, `nome`, `endereco`, `idDono`, lista de entregadores. Subclasses: Restaurante (tipoCozinha), Mercado (abre, fecha, tipoMercado) e Farmacia (aberto24Horas, numeroFuncionarios).
- **Produto:** `id`, `idEmpresa`, `nome`, `valor`, `categoria`.
- **Pedido:** `numero`, `idCliente`, `idEmpresa`, `estado` (ABERTO, PREPARANDO, PRONTO, ENTREGANDO, ENTREGUE), lista de IDs de produtos.
- **Entrega:** `id`, `idPedido`, `idEntregador`, `destino`, `entregue`.
- **EstadoPedido (enum):** Define as fases do pedido e controla as transições permitidas.

### 3.2 Serviços

- **Database:** Singleton. Mantém listas em memória e gerencia a persistência em XML. Fornece métodos de adição, atualização e consulta.
- **Validador:** Validações estáticas para campos como nome, email, senha, CPF (formato 14 caracteres), valor, categoria e horários (HH:MM).
- **UsuarioService:** Criação de clientes, donos e entregadores; login; consulta de atributos. Garante a unicidade de email e placa (entregadores). Realiza validações antes de verificar duplicidade.
- **EmpresaService:** Criação de restaurantes, mercados e farmácias. Regras de unicidade: mesmo dono pode ter mesmo nome em endereços diferentes; mesmo nome por donos diferentes é proibido. Duplicidade exata (nome + endereço) gera erro específico. Métodos para listar empresas de um dono/entregador, cadastrar entregadores, alterar funcionamento de mercado.
- **ProdutoService:** Criação, edição e listagem de produtos. Impede duplicidade de nome na mesma empresa.
- **PedidoService:** Controle do ciclo de vida do pedido (abrir, adicionar produto, fechar, liberar, remover produto). Impede ações em estados inválidos. Retorna informações do pedido formatadas (cliente, estado, produtos, valor).
- **EntregaService:** Criação de entrega (vincula entregador e pedido liberado). Prioridade para pedidos de farmácia na obtenção de pedidos (obterPedido). Finalização de entrega (altera estado para ENTREGUE). Verifica se entregador está ocupado.

### 3.3 Facade

A classe `Facade` (em `MyFood.Facade`) implementa todos os comandos esperados pelos scripts de teste. Os métodos têm exatamente as assinaturas requeridas e delegam a execução aos serviços correspondentes.

### 3.4 TestRunner

Como o EasyAccept não estava disponível, foi desenvolvido um executor próprio (`tests.TestRunner`) que interpreta os scripts `.txt`, substitui variáveis e compara os resultados com as expectativas (`expect` e `expectError`). Ele cobre todos os comandos da linguagem e foi validado com sucesso em todos os arquivos de teste.

---

## 4. Padrões de Projeto Adotados

### 4.1 Singleton – `Database`
Garante uma única instância do repositório de dados, centralizando o estado e a persistência. Todos os serviços acessam a mesma instância via `Database.getInstance()`.

### 4.2 Factory Method – Serviços de criação
Os métodos `criarCliente`, `criarDonoEmpresa`, `criarEntregador` (em `UsuarioService`) e `criarRestaurante`, `criarMercado`, `criarFarmacia` (em `EmpresaService`) atuam como fábricas, decidindo qual subclasse instanciar com base nos parâmetros fornecidos.

### 4.3 Strategy / State – `EstadoPedido`
O ciclo de vida do pedido é controlado pela enumeração `EstadoPedido`. Cada método do `PedidoService` verifica o estado atual antes de permitir uma operação, seguindo um comportamento típico do padrão State (embora simplificado por condicionais).

### 4.4 DAO (Data Access Object) – `Database`
A classe `Database` abstrai o acesso aos dados, escondendo detalhes da serialização XML. Os serviços manipulam apenas os métodos públicos (`addUsuario`, `updatePedido`, etc.).

---

## 5. Como Compilar e Executar

### Compilação


dir /s /b *.java > sources.txt
javac -d bin @sources.txt


### Execução da Demonstração


java -cp bin MyFood.Main



### Execução dos Testes de Aceitação



### Execução dos Testes de Aceitação


java -cp bin tests.TestRunner tests/us1_1.txt tests/us1_2.txt tests/us2_1.txt tests/us2_2.txt tests/us3_1.txt tests/us3_2.txt tests/us4_1.txt tests/us4_2.txt tests/us5.txt tests/us6.txt tests/us7.txt tests/us8.txt


Todos os oito testes foram executados com sucesso, validando as funcionalidades da Milestone 1 e 2.

---

## 6. Considerações Finais

O sistema atende a todos os requisitos especificados nas User Stories 1 a 8, incluindo:

- Criação de contas (cliente, dono, entregador) com validações e unicidade.
- Criação de empresas (restaurante, mercado, farmácia) com regras de duplicidade.
- Gerenciamento de produtos.
- Ciclo completo de pedidos (abertura, adição/remoção de itens, fechamento, liberação e entrega).
- Priorização de pedidos de farmácia no sistema de entregas.

O projeto foi desenvolvido com princípios de orientação a objetos e padrões de projeto, resultando em um código modular, coeso e de fácil manutenção.
