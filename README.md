# MyFood

Sistema de delivery desenvolvido em Java para a disciplina de Programacao Orientada a Objetos.

O MyFood permite cadastrar usuarios, empresas, produtos, pedidos e entregas. O projeto foi organizado para ser validado por testes de aceitacao com EasyAccept, usando a classe `MyFood.Facade` como ponto de entrada.

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Status](https://img.shields.io/badge/Testes-passando-brightgreen)
![EasyAccept](https://img.shields.io/badge/EasyAccept-configurado-lightgrey)

## Sumario

- [Funcionalidades](#funcionalidades)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Arquitetura](#arquitetura)
- [Como Executar](#como-executar)
- [Testes](#testes)
- [Relatorio](#relatorio)

## Funcionalidades

- Criacao de contas para cliente, dono de empresa e entregador.
- Login e consulta de atributos de usuarios.
- Cadastro de restaurantes, mercados e farmacias.
- Cadastro, edicao, consulta e listagem de produtos.
- Criacao e gerenciamento de pedidos.
- Adicao e remocao de produtos em pedidos.
- Fluxo de pedido: aberto, preparando, pronto, entregando e entregue.
- Cadastro de entregadores em empresas.
- Criacao, consulta e finalizacao de entregas.
- Persistencia automatica em XML no arquivo `myfood_data.xml`.

## Estrutura do Projeto

```text
.
|-- lib/
|   `-- easyaccept.jar
|-- MyFood/
|   |-- exceptions/
|   |   `-- MyFoodException.java
|   |-- models/
|   |   |-- Usuario.java
|   |   |-- Cliente.java
|   |   |-- DonoEmpresa.java
|   |   |-- Entregador.java
|   |   |-- Empresa.java
|   |   |-- Restaurante.java
|   |   |-- Mercado.java
|   |   |-- Farmacia.java
|   |   |-- Produto.java
|   |   |-- Pedido.java
|   |   |-- Entrega.java
|   |   `-- EstadoPedido.java
|   |-- services/
|   |   |-- Database.java
|   |   |-- Validador.java
|   |   |-- UsuarioService.java
|   |   |-- EmpresaService.java
|   |   |-- ProdutoService.java
|   |   |-- PedidoService.java
|   |   `-- EntregaService.java
|   |-- Facade.java
|   `-- Main.java
|-- tests/
|   |-- TestRunner.java
|   |-- us1_1.txt
|   |-- us1_2.txt
|   |-- us2_1.txt
|   |-- us2_2.txt
|   |-- us3_1.txt
|   |-- us3_2.txt
|   |-- us4_1.txt
|   `-- us4_2.txt
|-- relatorio/
|   `-- README.md
|-- sources.txt
`-- README.md
```

## Arquitetura

O projeto segue uma divisao simples em camadas:

- `Main`: executa os testes de aceitacao com EasyAccept.
- `Facade`: expoe os metodos chamados pelos testes e delega para os services.
- `services`: concentram as regras de negocio.
- `models`: representam as entidades do dominio.
- `Database`: centraliza o armazenamento em memoria e a persistencia em XML.

Principais padroes usados:

- `Facade`: interface unica para os testes.
- `Singleton`: instancia unica de `Database`.
- `Service Layer`: separacao das regras de negocio por area.
- `DAO/Repository`: acesso a dados centralizado em `Database`.
- `Factory Method` simplificado: criacao controlada de usuarios e empresas.

Mais detalhes estao no [relatorio do projeto](relatorio/README.md).

## Como Executar

Os comandos abaixo consideram o PowerShell na raiz do projeto.

### 1. Compilar

```powershell
javac -cp "lib\easyaccept.jar" -d bin (Get-Content sources.txt)
```

### 2. Executar a Main

```powershell
java -cp "bin;lib\easyaccept.jar" MyFood.Main
```

A `Main` executa todos os scripts de teste de `us1_1` ate `us4_2`.

## Testes

### EasyAccept

Para executar pelo EasyAccept usando a `Main`:

```powershell
java -cp "bin;lib\easyaccept.jar" MyFood.Main
```

Tambem e possivel executar um script especifico:

```powershell
java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept MyFood.Facade tests/us1_1.txt
```

### TestRunner local

O projeto tambem possui um executor proprio para os scripts de teste:

```powershell
java -cp bin tests.TestRunner tests/us1_1.txt tests/us1_2.txt tests/us2_1.txt tests/us2_2.txt tests/us3_1.txt tests/us3_2.txt tests/us4_1.txt tests/us4_2.txt
```

## Relatorio

O relatorio completo esta em:

[relatorio/README.md](relatorio/README.md)

Ele descreve a arquitetura, os principais componentes e os padroes de projeto adotados.
