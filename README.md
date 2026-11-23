# 🏋️ Sistema de Cadastro para Academias

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

Aplicativo Android (Java) para cadastrar, listar, buscar, editar e excluir **alunos** de uma academia. Os dados ficam salvos localmente no **SQLite** do próprio aparelho.

- **Banco de dados:** `academia.db` (versão 1)
- **Pacote:** `com.academia.app`
- **Telas:** Menu inicial → Cadastro / Lista → Edição
- **Android mínimo:** API 24 (Android 7.0)

## 📑 Sumário

1. [Banco de dados](#1-banco-de-dados)
2. [Principais classes](#2-principais-classes)
3. [Comunicação entre as telas](#3-comunicação-entre-as-telas)
4. [Conexão com o SQLite](#4-conexão-com-o-sqlite)
5. [Operações CRUD e sua relação com os métodos REST](#5-operações-crud-e-sua-relação-com-os-métodos-rest)
6. [Como os dados aparecem na interface](#6-como-os-dados-aparecem-na-interface)
7. [Persistência dos dados](#7-persistência-dos-dados)
8. [Como executar](#8-como-executar)

---

## 1. Banco de dados

### 1.1 Nome da tabela

**`alunos`**

### 1.2 Colunas, tipos, chave primária e finalidade

| Coluna | Tipo | Restrições | Finalidade |
|---|---|---|---|
| `id_aluno` | INTEGER | **PRIMARY KEY**, AUTOINCREMENT | Identificador único do aluno, gerado automaticamente pelo banco |
| `nome` | TEXT | NOT NULL | Nome completo do aluno |
| `cpf` | TEXT | NOT NULL, **UNIQUE** | CPF (11 dígitos). É único, então o mesmo CPF não pode ser cadastrado duas vezes |
| `telefone` | TEXT | NOT NULL | Telefone de contato |
| `email` | TEXT | opcional | E-mail do aluno (validado no formulário quando preenchido) |
| `endereco` | TEXT | opcional | Endereço residencial |
| `data_nascimento` | TEXT | NOT NULL | Data de nascimento (formato `AAAA-MM-DD`) |
| `modalidade` | TEXT | NOT NULL | Atividade praticada (Musculação, Crossfit, Pilates, Spinning, Natação, Muay Thai, Dança) |
| `tipo_plano` | TEXT | NOT NULL | Plano contratado (Mensal, Trimestral, Semestral, Anual) |
| `valor_mensalidade` | REAL | NOT NULL | Valor da mensalidade em reais (precisa ser maior que zero) |
| `data_matricula` | TEXT | NOT NULL | Data em que o aluno se matriculou |
| `validade_plano` | TEXT | NOT NULL | Data em que o plano vence |
| `situacao` | TEXT | NOT NULL | Status do aluno (Ativo, Inativo, Trancado) |

> **Chave primária:** `id_aluno`. O `AUTOINCREMENT` faz o SQLite gerar o número sozinho a cada INSERT.
> O SQLite não tem um tipo próprio para datas, por isso elas são salvas como `TEXT` no padrão ISO (`AAAA-MM-DD`), o que também permite ordenar e comparar as datas corretamente.

### 1.3 Comando SQL de criação (CREATE TABLE)

Fica em `DatabaseHelper.java` e é executado no método `onCreate()`:

```sql
CREATE TABLE alunos (
    id_aluno          INTEGER PRIMARY KEY AUTOINCREMENT,
    nome              TEXT NOT NULL,
    cpf               TEXT NOT NULL UNIQUE,
    telefone          TEXT NOT NULL,
    email             TEXT,
    endereco          TEXT,
    data_nascimento   TEXT NOT NULL,
    modalidade        TEXT NOT NULL,
    tipo_plano        TEXT NOT NULL,
    valor_mensalidade REAL NOT NULL,
    data_matricula    TEXT NOT NULL,
    validade_plano    TEXT NOT NULL,
    situacao          TEXT NOT NULL
);
```

No código Java:

```java
private static final String CREATE_TABLE =
        "CREATE TABLE " + TABELA + " (" +
        "id_aluno INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "nome TEXT NOT NULL, " +
        "cpf TEXT NOT NULL UNIQUE, " +
        ...
        "situacao TEXT NOT NULL)";

@Override
public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE);
}
```

---

## 2. Principais classes

| Classe / Recurso | Tipo | Responsabilidade |
|---|---|---|
| `MainActivity` | Activity | Tela inicial (menu) com os botões **Cadastrar aluno** e **Listar alunos** |
| `CadastroActivity` | Activity | Tela de cadastro de um novo aluno (**INSERT**) |
| `ListaActivity` | Activity | Lista os alunos, faz a busca por nome/CPF e permite excluir (**SELECT** / **DELETE**) |
| `EditarActivity` | Activity | Carrega um aluno, permite alterar ou excluir (**SELECT** / **UPDATE** / **DELETE**) |
| `DatabaseHelper` | `SQLiteOpenHelper` | Cria, abre e atualiza o banco e concentra todas as operações CRUD |
| `SQLiteDatabase` | Android | Objeto que executa as leituras e escritas no banco |
| `ContentValues` | Android | Organiza os pares coluna → valor usados no INSERT e no UPDATE |
| `Cursor` | Android | Percorre as linhas retornadas por um SELECT |
| `AlunoAdapter` | `ArrayAdapter<Aluno>` | Liga a lista de alunos vinda do banco ao `ListView` da tela |
| `Aluno` | Model | Representa um aluno (cada objeto corresponde a uma linha da tabela) |
| `FormHelper` | Auxiliar | Formulário compartilhado entre cadastro e edição: lê, valida e preenche os campos |

### 2.1 Métodos implementados

**`DatabaseHelper`**
| Método | O que faz |
|---|---|
| `onCreate(db)` | Executa o `CREATE TABLE` na primeira vez que o banco é aberto |
| `onUpgrade(db, old, new)` | Quando a versão muda, apaga a tabela (`DROP TABLE`) e recria |
| `inserir(Aluno)` | INSERT. Retorna o id gerado, ou `-1` se der erro (ex.: CPF duplicado) |
| `listar(String busca)` | SELECT de todos os alunos, ou filtrado por nome/CPF, em ordem alfabética |
| `buscarPorId(int id)` | SELECT de um único aluno pelo `id_aluno` |
| `atualizar(Aluno)` | UPDATE pelo `id_aluno`. Retorna o número de linhas alteradas, ou `-1` em caso de erro |
| `excluir(int id)` | DELETE pelo `id_aluno`. Retorna o número de linhas removidas |
| `toValues(Aluno)` | Converte um `Aluno` em `ContentValues` |
| `fromCursor(Cursor)` | Converte a linha atual do `Cursor` em um objeto `Aluno` |

**`FormHelper`**: `lerEValidar()` (valida os campos e devolve um `Aluno`, ou `null` se houver erro), `preencher(Aluno)` (preenche o formulário na edição), além de configurar os Spinners (modalidade, plano, situação) e os seletores de data (`DatePickerDialog`).

**`AlunoAdapter`**: `getView()` monta cada item da lista (nome, modalidade • plano • valor, situação e validade) e o botão **Excluir** de cada linha.

**`ListaActivity`**: `carregar()` (busca no banco e atualiza a lista), `confirmarExclusao(Aluno)` e `onResume()` (recarrega a lista ao voltar de outra tela).

---

## 3. Comunicação entre as telas

A navegação é feita com **`Intent`**:

```
MainActivity ──(Cadastrar)──► CadastroActivity ──finish()──► volta
     │
     └──(Listar)──► ListaActivity ──(Novo)──► CadastroActivity
                         │
                         └──(toque no item)──► EditarActivity
                                 putExtra("id_aluno", id)
```

- Para abrir uma tela: `startActivity(new Intent(this, CadastroActivity.class))`.
- Para **passar dados** entre telas, a `ListaActivity` envia o id do aluno escolhido:
  ```java
  Intent i = new Intent(this, EditarActivity.class);
  i.putExtra("id_aluno", a.getId());
  startActivity(i);
  ```
  E a `EditarActivity` recebe: `getIntent().getIntExtra("id_aluno", -1)`.
- Para voltar, usamos `finish()`, que fecha a tela atual. Ao voltar para a lista, o `onResume()` chama `carregar()`, então a lista já aparece com os dados atualizados.

---

## 4. Conexão com o SQLite

1. Cada tela cria um `DatabaseHelper`: `new DatabaseHelper(this)`.
2. O construtor informa o nome e a versão do banco: `super(context, "academia.db", null, 1)`.
3. Quando chamamos `getWritableDatabase()` (para escrever) ou `getReadableDatabase()` (para ler), o Android:
   - **cria** o arquivo `academia.db` e chama `onCreate()` se ele ainda não existir;
   - **abre** o banco se ele já existir;
   - chama `onUpgrade()` se a versão no código for maior que a versão salva no aparelho.
4. Os dois métodos retornam um `SQLiteDatabase`, que é o objeto usado para executar os comandos.

### Importância do `SQLiteOpenHelper`

Sem ele, teríamos que verificar manualmente se o arquivo do banco existe, criar a tabela, controlar versões e gerenciar a abertura da conexão. O `SQLiteOpenHelper` faz tudo isso automaticamente:
- garante que a tabela seja criada **uma única vez** (`onCreate`);
- controla a **versão** do banco e a atualização do esquema (`onUpgrade`);
- gerencia a abertura da conexão e reaproveita a mesma instância.

No projeto, a classe `DatabaseHelper` estende `SQLiteOpenHelper` e também concentra todos os métodos de CRUD, por isso não foi necessária uma classe DAO separada.

---

## 5. Operações CRUD e sua relação com os métodos REST

> **Observação:** o aplicativo **não usa API REST nem requisições HTTP**. Os dados ficam no SQLite do próprio aparelho, sem servidor. Mesmo assim, as operações seguem o mesmo padrão **CRUD** que uma API REST usa. A tabela abaixo mostra a equivalência para facilitar a explicação.

| CRUD | Método REST (equivalente) | SQL | Método no app | Classe Android usada | Tela |
|---|---|---|---|---|---|
| **Create** | `POST /alunos` | `INSERT` | `inserir(Aluno)` | `ContentValues` + `db.insert()` | CadastroActivity |
| **Read** (todos) | `GET /alunos?busca=...` | `SELECT ... WHERE nome LIKE ? OR cpf LIKE ?` | `listar(busca)` | `db.query()` + `Cursor` | ListaActivity |
| **Read** (um) | `GET /alunos/{id}` | `SELECT ... WHERE id_aluno = ?` | `buscarPorId(id)` | `db.query()` + `Cursor` | EditarActivity |
| **Update** | `PUT /alunos/{id}` | `UPDATE ... WHERE id_aluno = ?` | `atualizar(Aluno)` | `ContentValues` + `db.update()` | EditarActivity |
| **Delete** | `DELETE /alunos/{id}` | `DELETE ... WHERE id_aluno = ?` | `excluir(id)` | `db.delete()` | ListaActivity / EditarActivity |

Também é possível comparar os retornos com os códigos de status HTTP:

| Situação no app | Retorno | Equivalente REST |
|---|---|---|
| Aluno cadastrado | `inserir()` retorna o id (> 0) | `201 Created` |
| CPF já cadastrado | `inserir()` retorna `-1` / `atualizar()` retorna `-1` | `409 Conflict` |
| Campo inválido no formulário | `lerEValidar()` retorna `null` e mostra o erro no campo | `400 Bad Request` |
| Aluno não encontrado na edição | `buscarPorId()` retorna `null` | `404 Not Found` |
| Atualização/exclusão concluída | retorna nº de linhas > 0 | `200 OK` / `204 No Content` |

### 5.1 INSERT — equivalente ao `POST`

```java
public long inserir(Aluno a) {
    SQLiteDatabase db = getWritableDatabase();
    return db.insert("alunos", null, toValues(a));
}
```
1. O usuário preenche o formulário e toca em **Salvar**.
2. `FormHelper.lerEValidar()` valida os campos e monta o objeto `Aluno`.
3. `toValues()` coloca os dados em um `ContentValues` (coluna → valor).
4. `db.insert()` grava no banco. Se o CPF já existir, a restrição `UNIQUE` impede a gravação e o método retorna `-1`, e o app mostra *"Erro: CPF já cadastrado!"*.

### 5.2 SELECT — equivalente ao `GET`

```java
c = db.query("alunos", null, "nome LIKE ? OR cpf LIKE ?",
        new String[]{like, like}, null, null, "nome COLLATE NOCASE ASC");
while (c.moveToNext()) lista.add(fromCursor(c));
c.close();
```
- Sem busca, retorna todos os alunos em ordem alfabética.
- Com busca, filtra por nome **ou** CPF usando `LIKE` (a lista é atualizada a cada letra digitada, através de um `TextWatcher`).
- O `Cursor` percorre o resultado linha por linha, e `fromCursor()` transforma cada linha em um `Aluno`.
- Os `?` são **parâmetros**: os valores são passados separadamente, o que protege contra *SQL Injection*.

### 5.3 UPDATE — equivalente ao `PUT`

```java
return db.update("alunos", toValues(a), "id_aluno = ?",
        new String[]{String.valueOf(a.getId())});
```
- A `EditarActivity` recebe o id pela Intent, carrega o aluno (`buscarPorId`) e preenche o formulário.
- Ao salvar, todos os campos são enviados de novo, substituindo o registro inteiro (por isso a equivalência é com `PUT`, e não `PATCH`).

### 5.4 DELETE — equivalente ao `DELETE`

```java
return db.delete("alunos", "id_aluno = ?", new String[]{String.valueOf(id)});
```
- Pode ser feito pelo botão **Excluir** de cada item da lista ou pela tela de edição.
- Antes de excluir, um `AlertDialog` pede a confirmação do usuário.

---

## 6. Como os dados aparecem na interface

1. `ListaActivity.carregar()` chama `db.listar(...)`, que retorna uma `List<Aluno>`.
2. Essa lista é entregue ao **`AlunoAdapter`**, que é colocado no `ListView`.
3. Para cada aluno, o `getView()` do adapter infla o layout `item_aluno.xml` e preenche:
   - **Nome**
   - **Modalidade • Plano • R$ valor**
   - **Situação | Validade**
   - Botão **Excluir**
4. Se a lista estiver vazia, aparece uma mensagem (`tvVazio`).
5. Tocar em um item abre a tela de edição daquele aluno.

Fluxo resumido: **Banco → Cursor → Aluno (Model) → List\<Aluno\> → AlunoAdapter → ListView**

---

## 7. Persistência dos dados

- Os dados são gravados no arquivo **`academia.db`**, na pasta interna do app (`/data/data/com.academia.app/databases/`).
- Eles **continuam salvos** depois que o app é fechado ou o celular é reiniciado. Só são apagados se o app for desinstalado ou se os dados do app forem limpos.
- Toda alteração (insert/update/delete) é gravada **na hora** no banco, e a lista sempre lê os dados direto do banco, então o que aparece na tela é sempre o que está salvo.
- As restrições do banco (`NOT NULL`, `UNIQUE`, `PRIMARY KEY`) garantem a integridade, junto com as validações do formulário (CPF com 11 dígitos, e-mail válido, valor maior que zero, campos obrigatórios).

---

## 8. Como executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/jppinaffi/crud-academia.git
   ```
2. Abra a pasta no **Android Studio**.
3. Espere o Gradle sincronizar.
4. Rode em um emulador ou em um celular com Android 7.0 (API 24) ou superior.

```
app/src/main/java/com/academia/app/
├── MainActivity.java       # Menu inicial
├── CadastroActivity.java   # Cadastro (INSERT)
├── ListaActivity.java      # Listagem e busca (SELECT / DELETE)
├── EditarActivity.java     # Edição (UPDATE / DELETE)
├── DatabaseHelper.java     # SQLiteOpenHelper + CRUD
├── Aluno.java              # Model
├── AlunoAdapter.java       # Adapter do ListView
└── FormHelper.java         # Validação e preenchimento do formulário
```
