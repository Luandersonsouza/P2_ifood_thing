# 🍔 MyFood - Sistema de Delivery

[![Java Version](https://img.shields.io/badge/java-17%2B-blue)]()

Sistema desenvolvido para a disciplina de Programação Orientada a Objetos.  
O MyFood oferece cadastro de usuários (cliente, dono de empresa e entregador), empresas (restaurantes, mercados e farmácias), produtos, pedidos e entregas.

---

## 📁 Estrutura do Projeto


├── lib/ # Dependências (EasyAccept.jar)
├── relatorio/ # Relatório do projeto
│ └── README.md
├── MyFood/ # Código-fonte principal
│ ├── exceptions/ # Exceções personalizadas
│ ├── models/ # Classes de domínio
│ ├── services/ # Lógica de negócio e persistência
│ ├── Facade.java # Fachada para os testes de aceitação
│ └── Main.java # Demonstração simples do sistema
├── tests/ # Scripts de teste do EasyAccept
│ ├── us*.txt
│ └── README.md
└── README.md # Este arquivo


---

## 🚀 Como compilar e executar

### Compilação
```bash
mkdir bin
javac -d bin MyFood/**/*.java

java -cp bin MyFood.Main

java -cp "bin;lib/EasyAccept.jar" easyaccept.EasyAccept MyFood.Facade tests/us1.txt

🎯 Funcionalidades

    Criação de contas: Cliente, Dono de Empresa, Entregador

    Criação de empresas: Restaurante, Mercado, Farmácia

    Gerenciamento de produtos

    Abertura e fechamento de pedidos

    Ciclo de vida do pedido: aberto → preparando → pronto → entregando → entregue

    Sistema de entregas com prioridade para farmácias

    Persistência automática em XML