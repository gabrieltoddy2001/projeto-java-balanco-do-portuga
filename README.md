## Balanço do Portuga – Sistema de Aluguel de Veículos</h1>

---

# Autores

- **Gabryel Rosa Bomfim**  
- **Gabriel dos Santos Silva**

**Professor Orientador:** André Portugal  
Fundação Visconde de Cairu – Salvador (BA)  
Semestre: 2025.2  

---

## Introdução

O projeto **Balanço do Portuga** tem como objetivo desenvolver um **sistema orientado a objetos** que simule o funcionamento de uma **locadora de veículos**.

A aplicação foi desenvolvida em **Java SE**, utilizando **NetBeans** e **MySQL**, com foco em **modularidade, escalabilidade e aplicação de POO**.
O sistema gerencia **cadastro de carros, clientes, contratos, reservas e pagamentos**, além de gerar relatórios administrativos.

---

## Funcionalidades Principais

| Módulo | Funcionalidades |
|--------|------------------|
| **Clientes** | Cadastro, edição, consulta e exclusão e pagamentos |
| **Funcionários** | Gestão de usuários internos e controle de acesso |
| **Veículos** | Cadastro, controle de disponibilidade e histórico |
| **Reservas** | Registro de locações, devoluções e cancelamentos |
| **Pagamentos** | Processamento via PIX/cartão e controle financeiro |
| **Relatórios** | Geração de relatórios sobre contratos e inadimplência |

---

## Tecnologias Utilizadas

| Categoria | Ferramenta |
|------------|-------------|
| **Linguagem** | Java SE 21+ |
| **IDE** | NetBeans |
| **Banco de Dados** | MySQL |
| **Interface Gráfica** | Swing |
| **Controle de Versão** | Git & GitHub |
| **Metodologia** | Scrum (Product Backlog / Sprint Backlog) |

---

## Product Backlog (Resumo)

| ID | Requisito / Item                     | Tipo           | Tempo (dias) |
| -- | ------------------------------------ | -------------- | ------------ |
| 01 | Cadastro de Veículos                 | Funcionalidade | 3            |
| 02 | Edição e exclusão de Veículos        | Funcionalidade | 2            |
| 03 | Visualizar veículos disponíveis      | Funcionalidade | 2            |
| 04 | Cadastro de Usuário (Cliente)        | Funcionalidade | 3            |
| 05 | Login de Usuário                     | Funcionalidade | 2            |
| 06 | Cadastro e gestão de Funcionários    | Funcionalidade | 4            |
| 07 | Realizar reserva de veículo          | Funcionalidade | 3            |
| 08 | Alterar ou cancelar reserva          | Funcionalidade | 2            |
| 09 | Registrar retirada e devolução       | Funcionalidade | 5            |
| 10 | Relatórios de vendas/aluguéis        | Funcionalidade | 2            |
| 11 | Realizar pagamentos                  | Funcionalidade | 2            |
| 12 | Visualizador de pagamentos           | Funcionalidade | 2            |
| 13 | Interface de cadastro e consulta     | Interface      | 2            |
| 14 | Interface de Funcionários e Usuários | Interface      | 2            |
| 15 | Verificar e corrigir bugs            | Manutenção     | 7            |

**Tempo Total Estimado:** 54 dias

---

## Cronograma de Desenvolvimento

| Mês          | Atividades                                            |
| ------------ | ----------------------------------------------------- |
| **Setembro** | Cadastro de usuários, login, e otimização de reservas |
| **Outubro**  | Gestão de funcionários, correção de bugs e relatórios |
| **Novembro** | Ajustes finais, testes e entrega da versão final      |

---

## Requisitos do Sistema

### Cliente

* RF001 – Incluir Cliente
* RF002 – Consultar Cliente
* RF003 – Alterar Cliente
* RF004 – Excluir Cliente
* RF005 – Pesquisar Cliente
* RNF001 – Todos os campos obrigatórios devem ser preenchidos
* RNF002 – Não é permitido incluir cliente com CPF repetido
* RNF003 – CNH vencida, suspensa ou cassada não é aceita

### Funcionário

* RF006 – Incluir Funcionário
* RF007 – Consultar Funcionário
* RF008 – Alterar Funcionário
* RF009 – Excluir Funcionário
* RF010 – Pesquisar Funcionário
* RNF004 – Campos obrigatórios devem ser preenchidos
* RNF005 – Não permitir CPF duplicado

### Serviço

* RF011 – Incluir Serviço
* RF012 – Consultar Serviço
* RF013 – Alterar Serviço
* RF014 – Excluir Serviço
* RF015 – Pesquisar Serviço
* RNF007 – Todos os campos obrigatórios devem ser preenchidos

### Veículos

* RF016 – Incluir Veículo
* RF017 – Consultar Veículo
* RF018 – Alterar Veículo
* RF019 – Excluir Veículo
* RF020 – Pesquisar Veículo
* RNF008 – Todos os campos obrigatórios devem ser preenchidos

### Pagamentos

* RF021 – Registrar Pagamento
* RF022 – Cancelar Pagamento
* RNF009 – Campos obrigatórios devem ser preenchidos
* RNF010 – Não registrar pagamento com valor diferente da dívida

### Relatórios

* RNF011 – Todos os filtros obrigatórios devem ser preenchidos
* RNF012 – Data final não pode ser menor que a inicial

---

## Regras de Negócio

| Código | Descrição                                                 |
| ------ | --------------------------------------------------------- |
| RN001  | Interface amigável e responsiva (acesso via desktop)      |
| RN002  | Sistema seguro com autenticação e autorização de usuários |

### Regras dos Requisitos Não Funcionais

* CNH deve estar **válida** nas datas de início e término da locação.
* CNH **categoria B** para carros e **categoria A** para motos.
* Se a CNH estiver vencida, o sistema impede a reserva e exibe erro.

---

## Conceitos Aplicados

* **POO:** Encapsulamento, Herança e Polimorfismo
* **Padrão MVC:** Separação entre interface, lógica e dados
* **CRUD Completo:** Operações em Clientes, Veículos, Reservas e Pagamentos
* **SQL Relacional:** Tabelas normalizadas e chaves estrangeiras
* **Metodologias Ágeis:** Planejamento via Backlog e Sprints

---

## Referências

* **LARMAN, C.** *Utilizando UML e Padrões.* [s.l: s.n.]
* **Querobolsa.** [Requisitos funcionais e não funcionais](https://querobolsa.com.br/revista/requisitos-funcionais-e-nao-funcionais)
* **PlantUML.** [Open-source UML tool](https://plantuml.com/)
* **Lucidchart.** [Modelos e exemplos de diagramas UML](https://www.lucidchart.com/blog/pt/modelos-e-exemplos-de-diagramas-uml)
* **Runrun.it.** [Backlog: o que é e como melhorar processos](https://blog.runrun.it/backlog/)
* **Scrum.org.** [What is a Sprint Backlog?](https://www.scrum.org/resources/what-is-a-sprint-backlog)

---

## **Autores**

**Gabriel Silva**
**Gabriel Rosa**
* Estudantes em Análise e Desenvolvimento de Sistemas - Faculdade Visconde de Cairu## Balanço do Portuga – Sistema de Aluguel de Veículos</h1>

---

# Autores

- **Gabryel Rosa Bomfim**  
- **Gabriel dos Santos Silva**

**Professor Orientador:** André Portuga  
Fundação Visconde de Cairu – Salvador (BA)  
Semestre: 2025.2  

---

## Introdução

O projeto **Balanço do Portuga** tem como objetivo desenvolver um **sistema orientado a objetos** que simule o funcionamento de uma **locadora de veículos**.

A aplicação foi desenvolvida em **Java SE**, utilizando **NetBeans** e **MySQL**, com foco em **modularidade, escalabilidade e aplicação de POO**.
O sistema gerencia **cadastro de carros, clientes, contratos, reservas e pagamentos**, além de gerar relatórios administrativos.

---

## Funcionalidades Principais

| Módulo | Funcionalidades |
|--------|------------------|
| **Clientes** | Cadastro, edição, consulta e exclusão e pagamentos |
| **Funcionários** | Gestão de usuários internos e controle de acesso |
| **Veículos** | Cadastro, controle de disponibilidade e histórico |
| **Reservas** | Registro de locações, devoluções e cancelamentos |
| **Pagamentos** | Processamento via PIX/cartão e controle financeiro |
| **Relatórios** | Geração de relatórios sobre contratos e inadimplência |

---

## Tecnologias Utilizadas

| Categoria | Ferramenta |
|------------|-------------|
| **Linguagem** | Java SE 21+ |
| **IDE** | NetBeans |
| **Banco de Dados** | MySQL |
| **Interface Gráfica** | Swing |
| **Controle de Versão** | Git & GitHub |
| **Metodologia** | Scrum (Product Backlog / Sprint Backlog) |

---

## Product Backlog (Resumo)

| ID | Requisito / Item                     | Tipo           | Tempo (dias) |
| -- | ------------------------------------ | -------------- | ------------ |
| 01 | Cadastro de Veículos                 | Funcionalidade | 3            |
| 02 | Edição e exclusão de Veículos        | Funcionalidade | 2            |
| 03 | Visualizar veículos disponíveis      | Funcionalidade | 2            |
| 04 | Cadastro de Usuário (Cliente)        | Funcionalidade | 3            |
| 05 | Login de Usuário                     | Funcionalidade | 2            |
| 06 | Cadastro e gestão de Funcionários    | Funcionalidade | 4            |
| 07 | Realizar reserva de veículo          | Funcionalidade | 3            |
| 08 | Alterar ou cancelar reserva          | Funcionalidade | 2            |
| 09 | Registrar retirada e devolução       | Funcionalidade | 5            |
| 10 | Relatórios de vendas/aluguéis        | Funcionalidade | 2            |
| 11 | Realizar pagamentos                  | Funcionalidade | 2            |
| 12 | Visualizador de pagamentos           | Funcionalidade | 2            |
| 13 | Interface de cadastro e consulta     | Interface      | 2            |
| 14 | Interface de Funcionários e Usuários | Interface      | 2            |
| 15 | Verificar e corrigir bugs            | Manutenção     | 7            |

**Tempo Total Estimado:** 54 dias

---

## Cronograma de Desenvolvimento

| Mês          | Atividades                                            |
| ------------ | ----------------------------------------------------- |
| **Setembro** | Cadastro de usuários, login, e otimização de reservas |
| **Outubro**  | Gestão de funcionários, correção de bugs e relatórios |
| **Novembro** | Ajustes finais, testes e entrega da versão final      |

---

## Requisitos do Sistema

### Cliente

* RF001 – Incluir Cliente
* RF002 – Consultar Cliente
* RF003 – Alterar Cliente
* RF004 – Excluir Cliente
* RF005 – Pesquisar Cliente
* RNF001 – Todos os campos obrigatórios devem ser preenchidos
* RNF002 – Não é permitido incluir cliente com CPF repetido
* RNF003 – CNH vencida, suspensa ou cassada não é aceita

### Funcionário

* RF006 – Incluir Funcionário
* RF007 – Consultar Funcionário
* RF008 – Alterar Funcionário
* RF009 – Excluir Funcionário
* RF010 – Pesquisar Funcionário
* RNF004 – Campos obrigatórios devem ser preenchidos
* RNF005 – Não permitir CPF duplicado

### Serviço

* RF011 – Incluir Serviço
* RF012 – Consultar Serviço
* RF013 – Alterar Serviço
* RF014 – Excluir Serviço
* RF015 – Pesquisar Serviço
* RNF007 – Todos os campos obrigatórios devem ser preenchidos

### Veículos

* RF016 – Incluir Veículo
* RF017 – Consultar Veículo
* RF018 – Alterar Veículo
* RF019 – Excluir Veículo
* RF020 – Pesquisar Veículo
* RNF008 – Todos os campos obrigatórios devem ser preenchidos

### Pagamentos

* RF021 – Registrar Pagamento
* RF022 – Cancelar Pagamento
* RNF009 – Campos obrigatórios devem ser preenchidos
* RNF010 – Não registrar pagamento com valor diferente da dívida

### Relatórios

* RNF011 – Todos os filtros obrigatórios devem ser preenchidos
* RNF012 – Data final não pode ser menor que a inicial

---

## Regras de Negócio

| Código | Descrição                                                 |
| ------ | --------------------------------------------------------- |
| RN001  | Interface amigável e responsiva (acesso via desktop)      |
| RN002  | Sistema seguro com autenticação e autorização de usuários |

### Regras dos Requisitos Não Funcionais

* CNH deve estar **válida** nas datas de início e término da locação.
* CNH **categoria B** para carros e **categoria A** para motos.
* Se a CNH estiver vencida, o sistema impede a reserva e exibe erro.

---

## Conceitos Aplicados

* **POO:** Encapsulamento, Herança e Polimorfismo
* **Padrão MVC:** Separação entre interface, lógica e dados
* **CRUD Completo:** Operações em Clientes, Veículos, Reservas e Pagamentos
* **SQL Relacional:** Tabelas normalizadas e chaves estrangeiras
* **Metodologias Ágeis:** Planejamento via Backlog e Sprints

---

## Referências

* **LARMAN, C.** *Utilizando UML e Padrões.* [s.l: s.n.]
* **Querobolsa.** [Requisitos funcionais e não funcionais](https://querobolsa.com.br/revista/requisitos-funcionais-e-nao-funcionais)
* **PlantUML.** [Open-source UML tool](https://plantuml.com/)
* **Lucidchart.** [Modelos e exemplos de diagramas UML](https://www.lucidchart.com/blog/pt/modelos-e-exemplos-de-diagramas-uml)
* **Runrun.it.** [Backlog: o que é e como melhorar processos](https://blog.runrun.it/backlog/)
* **Scrum.org.** [What is a Sprint Backlog?](https://www.scrum.org/resources/what-is-a-sprint-backlog)

---

## **Autores**

**Gabriel Silva**
**Gabriel Rosa**
* Estudantes em Análise e Desenvolvimento de Sistemas - Faculdade Visconde de Cairu
