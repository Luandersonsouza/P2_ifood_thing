# Relatorio do Projeto MyFood

## 1. Descricao Geral do Design Arquitetural

O MyFood e um sistema de delivery desenvolvido em Java para gerenciar usuarios, empresas, produtos, pedidos e entregas. A arquitetura foi organizada em camadas, separando a entrada dos comandos, as regras de negocio, os modelos de dominio e a persistencia dos dados.

A classe `Main` executa os testes de aceitacao com EasyAccept. Esses testes chamam a classe `Facade`, que funciona como ponto unico de entrada do sistema. A `Facade` recebe comandos como `criarUsuario`, `criarEmpresa`, `criarPedido` e `getAtributoUsuario`, e delega a execucao para os services especializados.

A camada de servicos concentra a logica de negocio. Cada service cuida de um conjunto de responsabilidades: usuarios, empresas, produtos, pedidos ou entregas. A camada de modelos representa as entidades principais do dominio, como `Usuario`, `Empresa`, `Produto`, `Pedido` e `Entrega`. A persistencia e centralizada em `Database`, que mantem os dados em memoria e salva automaticamente em XML no arquivo `myfood_data.xml`.

Essa divisao reduz o acoplamento entre as partes do sistema. Os testes nao precisam conhecer os detalhes internos dos services ou da persistencia; eles interagem apenas com a `Facade`. Ao mesmo tempo, os services nao precisam lidar diretamente com a sintaxe dos arquivos de teste, apenas com regras de negocio.

## 2. Principais Componentes e Interacoes

### 2.1 Main

A classe `Main` e responsavel por iniciar a validacao do sistema com EasyAccept. Ela chama todos os arquivos de teste existentes no projeto, de `tests/us1_1.txt` ate `tests/us8_2.txt`, sempre apontando para `MyFood.Facade`. Com isso, a execucao principal valida desde o cadastro inicial de usuarios e restaurantes ate os fluxos mais recentes de mercados, farmacias, entregadores e entregas.

Fluxo principal:

1. `Main` chama o EasyAccept.
2. EasyAccept le os scripts de teste.
3. EasyAccept invoca os metodos publicos da `Facade`.
4. A `Facade` delega as operacoes para os services.

### 2.2 Facade

A classe `Facade` e a interface publica do sistema para os testes de aceitacao. Ela expoe os metodos esperados pelos scripts, como:

- `zerarSistema`
- `criarUsuario`
- `login`
- `getAtributoUsuario`
- `criarEmpresa`
- `criarProduto`
- `criarPedido`
- `liberarPedido`
- `obterPedido`
- `criarEntrega`
- `getEntrega`
- `entregar`

A `Facade` nao concentra as regras completas de negocio. Seu papel principal e receber a chamada externa, escolher o service correto e retornar o resultado no formato esperado pelos testes.

### 2.3 Services

Os services representam a camada de regras de negocio:

- `UsuarioService`: cria clientes, donos de empresa e entregadores; realiza login; consulta atributos de usuarios; valida dados especificos de entregadores, como veiculo e placa unica.
- `EmpresaService`: cria restaurantes, mercados e farmacias; consulta empresas; cadastra entregadores; lista entregadores vinculados; lista empresas de um entregador; altera funcionamento de mercado.
- `ProdutoService`: cria, edita, consulta e lista produtos.
- `PedidoService`: cria pedidos, adiciona e remove produtos, fecha pedidos, libera pedidos para entrega e consulta seus atributos.
- `EntregaService`: cria entregas, consulta entregas, conclui entregas e obtem pedidos disponiveis para entregadores, priorizando pedidos de farmacia quando aplicavel.
- `Validador`: centraliza validacoes comuns, como nome, email, senha, endereco, CPF, placa, valor, categoria e horario.

Esses componentes interagem principalmente com `Database`, que guarda e atualiza os objetos do sistema.

### 2.4 Models

Os models representam as entidades do dominio:

- `Usuario`: classe abstrata base para `Cliente`, `DonoEmpresa` e `Entregador`.
- `Empresa`: classe abstrata base para `Restaurante`, `Mercado` e `Farmacia`.
- `Produto`: representa um item vendido por uma empresa.
- `Pedido`: representa uma compra feita por um cliente em uma empresa, mantendo cliente, empresa, produtos e estado.
- `Entrega`: representa a entrega de um pedido por um entregador, incluindo destino, pedido associado e situacao de conclusao.
- `EstadoPedido`: enum que representa os estados do pedido: aberto, preparando, pronto, entregando e entregue. O estado legado `LIBERADO` permanece no enum para compatibilidade com dados persistidos antigos, mas o fluxo atual usa `PRONTO`.

As classes abstratas `Usuario` e `Empresa` permitem reaproveitar atributos comuns e especializar comportamentos nas subclasses.

### 2.5 Database

`Database` centraliza o armazenamento do sistema. Ele mantem listas de usuarios, empresas, produtos, pedidos e entregas, alem dos contadores usados para gerar IDs e numeros de pedido.

Sempre que um objeto e adicionado ou atualizado, o `Database` salva os dados no arquivo `myfood_data.xml`. Assim, os services nao precisam conhecer os detalhes da serializacao XML.

## 3. Padroes de Projeto Adotados

### 3.1 Facade

**Nome do Padrao de Projeto:** Facade.

**Descricao Geral:** O padrao Facade fornece uma interface simples e unificada para acessar um conjunto de classes mais complexas. Em vez de o cliente conhecer varios objetos internos, ele chama uma unica classe, que coordena as operacoes necessarias.

**Problema Resolvido:** Sem uma fachada, os testes precisariam conhecer diretamente `UsuarioService`, `EmpresaService`, `ProdutoService`, `PedidoService`, `EntregaService` e as regras de formatacao de cada retorno. Isso aumentaria o acoplamento entre os testes e a implementacao interna.

**Identificacao da Oportunidade:** O EasyAccept espera chamar metodos publicos de uma unica classe. Como os scripts de teste usam comandos como `criarUsuario`, `criarEmpresa` e `getPedidos`, ficou natural concentrar essa interface em uma classe especifica.

**Aplicacao no Projeto:** A classe `MyFood.Facade` implementa todos os comandos usados pelos testes. Por exemplo, `criarUsuario(...)` delega para `UsuarioService`, `criarEmpresa(...)` delega para `EmpresaService`, `criarProduto(...)` delega para `ProdutoService`, e assim por diante. A `Facade` tambem adapta alguns retornos para o formato esperado pelos scripts, como em `getEmpresas`.

### 3.2 Singleton

**Nome do Padrao de Projeto:** Singleton.

**Descricao Geral:** O padrao Singleton garante que uma classe tenha apenas uma instancia durante a execucao do programa e fornece um ponto global de acesso a essa instancia.

**Problema Resolvido:** O sistema precisa que todos os services trabalhem sobre a mesma base de dados. Se cada service criasse seu proprio banco em memoria, usuarios criados em `UsuarioService` nao seriam encontrados por `PedidoService` ou `EmpresaService`.

**Identificacao da Oportunidade:** Ao observar que todos os services precisam consultar e atualizar as mesmas listas de usuarios, empresas, produtos, pedidos e entregas, foi identificada a necessidade de centralizar esse estado compartilhado.

**Aplicacao no Projeto:** A classe `Database` possui um atributo estatico `instance`, construtor privado e o metodo `getInstance()`. Services como `UsuarioService`, `EmpresaService`, `ProdutoService`, `PedidoService` e `EntregaService` acessam o banco por meio de `Database.getInstance()`. Assim, todos manipulam a mesma instancia.

### 3.3 Factory Method Simplificado

**Nome do Padrao de Projeto:** Factory Method, aplicado de forma simplificada.

**Descricao Geral:** O Factory Method centraliza a criacao de objetos, permitindo que a logica de instanciacao fique encapsulada em metodos especificos. Em vez de espalhar chamadas diretas a construtores por todo o sistema, a criacao passa por pontos controlados.

**Problema Resolvido:** Usuarios e empresas possuem tipos diferentes, mas compartilham estruturas comuns. Criar esses objetos diretamente em varias partes do sistema aumentaria duplicacao e deixaria as validacoes espalhadas.

**Identificacao da Oportunidade:** O projeto possui hierarquias como `Usuario -> Cliente/DonoEmpresa/Entregador` e `Empresa -> Restaurante/Mercado/Farmacia`. Como cada subtipo exige parametros e validacoes proprias, a criacao precisava ficar concentrada.

**Aplicacao no Projeto:** `UsuarioService` possui metodos como `criarCliente`, `criarDonoEmpresa` e `criarEntregador`. `EmpresaService` possui `criarRestaurante`, `criarMercado` e `criarFarmacia`. Esses metodos validam os dados, verificam regras de duplicidade e instanciam a subclasse correta antes de registrar o objeto no `Database`.

### 3.4 DAO / Repository

**Nome do Padrao de Projeto:** DAO/Repository.

**Descricao Geral:** O padrao DAO ou Repository cria uma camada de acesso a dados, escondendo detalhes de armazenamento e oferecendo metodos de consulta, insercao e atualizacao para o restante do sistema.

**Problema Resolvido:** Os services precisam salvar e buscar entidades, mas nao deveriam depender diretamente de detalhes como listas internas, contadores de IDs ou serializacao XML.

**Identificacao da Oportunidade:** Como o projeto precisa persistir dados e reutilizar a mesma base em diferentes services, foi necessario criar um componente responsavel apenas pelo armazenamento e recuperacao dos objetos.

**Aplicacao no Projeto:** A classe `Database` oferece metodos como `addUsuario`, `addEmpresa`, `addProduto`, `addPedido`, `addEntrega`, `updateUsuario`, `updateEmpresa`, `updateProduto`, `updatePedido` e `updateEntrega`. Ela tambem fornece metodos de consulta como `getUsuarios`, `getEmpresas`, `getProdutos`, `getPedidos` e `getEntregas`. Internamente, ela salva e carrega os dados usando XML, mas essa decisao fica escondida dos services.

### 3.5 State Simplificado

**Nome do Padrao de Projeto:** State, aplicado de forma simplificada.

**Descricao Geral:** O padrao State permite alterar o comportamento de um objeto de acordo com seu estado interno. Em uma implementacao classica, cada estado pode ser representado por uma classe. Neste projeto, a ideia foi aplicada de forma simplificada com uma enumeracao e validacoes nos services.

**Problema Resolvido:** Um pedido nao pode aceitar qualquer operacao em qualquer momento. Por exemplo, um pedido fechado nao deve receber novos produtos, e uma entrega concluida deve alterar o estado final do pedido.

**Identificacao da Oportunidade:** O dominio de pedidos possui um ciclo de vida claro: aberto, preparando, pronto, entregando e entregue. Isso indicou a necessidade de controlar transicoes e impedir operacoes invalidas.

**Aplicacao no Projeto:** A enum `EstadoPedido` representa os estados possiveis. `PedidoService` consulta esse estado antes de permitir operacoes como adicionar produto, remover produto, fechar pedido e liberar pedido. Quando um pedido e liberado, ele passa de `PREPARANDO` para `PRONTO`. `EntregaService` so cria entregas para pedidos prontos, muda o pedido para `ENTREGANDO` durante a entrega e altera o estado para `ENTREGUE` quando a entrega e finalizada.

### 3.6 Service Layer

**Nome do Padrao de Projeto:** Service Layer.

**Descricao Geral:** O padrao Service Layer organiza a logica de negocio em classes de servico. Ele separa as regras do dominio da interface externa e da camada de persistencia.

**Problema Resolvido:** Sem essa camada, a `Facade` ficaria muito grande e misturaria entrada dos testes, validacoes, regras de negocio, criacao de objetos e persistencia.

**Identificacao da Oportunidade:** O sistema possui varios grupos de regras independentes: usuarios, empresas, produtos, pedidos e entregas. Cada grupo tem validacoes e fluxos proprios, entao separar essas responsabilidades em services tornou a estrutura mais clara.

**Aplicacao no Projeto:** A `Facade` apenas encaminha chamadas. A logica fica em classes como `UsuarioService`, `EmpresaService`, `ProdutoService`, `PedidoService` e `EntregaService`. Por exemplo, `PedidoService` decide se um cliente pode criar pedido, se um produto pertence a empresa e se o pedido pode ser fechado.

## 4. Como Compilar e Executar

### 4.1 Compilar

No PowerShell, dentro da raiz do projeto:

```powershell
javac -cp "lib\easyaccept.jar" -d bin (Get-Content sources.txt)
```

### 4.2 Executar com EasyAccept

```powershell
java -cp "bin;lib\easyaccept.jar" MyFood.Main
```

A classe `Main` executa os seguintes arquivos:

- `tests/us1_1.txt`
- `tests/us1_2.txt`
- `tests/us2_1.txt`
- `tests/us2_2.txt`
- `tests/us3_1.txt`
- `tests/us3_2.txt`
- `tests/us4_1.txt`
- `tests/us4_2.txt`
- `tests/us5_1.txt`
- `tests/us5_2.txt`
- `tests/us6_1.txt`
- `tests/us6_2.txt`
- `tests/us7_1.txt`
- `tests/us7_2.txt`
- `tests/us8_1.txt`
- `tests/us8_2.txt`

### 4.3 Executar com TestRunner Local

O projeto tambem possui um executor proprio para os scripts:

```powershell
java -cp "bin;lib\easyaccept.jar" tests.TestRunner tests/us1_1.txt tests/us1_2.txt tests/us2_1.txt tests/us2_2.txt tests/us3_1.txt tests/us3_2.txt tests/us4_1.txt tests/us4_2.txt tests/us5_1.txt tests/us5_2.txt tests/us6_1.txt tests/us6_2.txt tests/us7_1.txt tests/us7_2.txt tests/us8_1.txt tests/us8_2.txt
```

## 5. Consideracoes Finais

O MyFood atende as funcionalidades cobertas pelos testes de aceitacao presentes no projeto, incluindo:

- criacao e consulta de usuarios;
- login;
- criacao e consulta de empresas;
- cadastro de restaurantes, mercados e farmacias, incluindo horario de funcionamento de mercados e dados especificos de farmacias;
- cadastro, edicao e listagem de produtos;
- abertura, fechamento, liberacao e consulta de pedidos;
- controle do ciclo de vida dos pedidos: aberto, preparando, pronto, entregando e entregue;
- cadastro de entregadores, com validacao de veiculo e placa unica;
- cadastro de entregadores em empresas e consulta das empresas vinculadas a cada entregador;
- obtencao de pedidos prontos para entrega, com prioridade para pedidos de farmacia;
- criacao de entregas com destino informado ou destino padrao do cliente;
- consulta de dados completos da entrega, incluindo cliente, empresa, pedido, entregador, destino e produtos;
- conclusao de entregas, liberando o entregador e marcando o pedido como entregue.

O design adotado favorece separacao de responsabilidades. A `Facade` simplifica o acesso externo, os services concentram regras de negocio, os models representam o dominio e o `Database` centraliza a persistencia. Essa organizacao torna o sistema mais facil de testar, manter e evoluir.
