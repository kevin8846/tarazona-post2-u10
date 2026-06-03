# tarazona-post2-u10

**Programación Web — Unidad 10: Pruebas de Software en Aplicaciones Web**  


## Tecnologías

| Tecnología          | Versión  | Rol                                              |
|---------------------|----------|--------------------------------------------------|
| Java                | 17       | Lenguaje                                         |
| Spring Boot         | 3.2.5    | Framework principal + Actuator (health check)    |
| H2 Database         | Runtime  | Base de datos en memoria                         |
| Selenium WebDriver  | 4.18.1   | Automatización de navegador (pruebas E2E)        |
| WebDriverManager    | 5.8.0    | Descarga ChromeDriver automáticamente            |
| Google Chrome       | Estable  | Navegador para tests headless                    |
| Postman             | v10+     | Diseño y ejecución manual de la colección        |
| Newman              | Latest   | Ejecución CLI de la colección Postman            |
| Node.js             | 18+      | Runtime para Newman                              |
| GitHub Actions      | -        | Pipeline CI/CD para ejecutar Newman              |
| JUnit 5             | 5.10.x   | Motor de pruebas unitarias heredado              |
| Mockito             | 5.x      | Mocks para pruebas unitarias heredadas           |

---

## Funcionamiento

### Selenium — Page Object Model
Los tests E2E están en `src/test/java/.../e2e/`. Se aplica el patrón **Page Object Model**:
- **`TareasPage`** — encapsula selectores y acciones de la lista de tareas
- **`NuevaTareaPage`** — encapsula el formulario de creación
- **`TareasE2ETest`** — tests que usan los Page Objects, Chrome en modo headless

Los selectores están declarados como constantes `By` privadas dentro de cada Page Object, nunca expuestos a los tests directamente.

### Postman — Colección API ToDoApp
La colección `postman/ColeccionToDo.json` contiene 5 requests en orden:
1. `POST /api/tareas` — crea tarea, guarda `tareaId` en variable de colección
2. `GET /api/tareas/{{tareaId}}` — verifica que existe
3. `PATCH /api/tareas/{{tareaId}}/completar` — marca como completada
4. `GET /api/tareas/{{tareaId}}` — verifica que `completada = true`
5. `GET /api/tareas/99999` — verifica respuesta 404

Cada request tiene test scripts que verifican status HTTP, cuerpo JSON y tiempo de respuesta < 500ms.

### Newman — GitHub Actions
El workflow `.github/workflows/api-tests.yml` ejecuta automáticamente la colección Postman en cada `push` o `pull_request`:
1. Compila el JAR con Maven
2. Inicia Spring Boot en segundo plano
3. Espera health check en `/actuator/health`
4. Ejecuta Newman con `postman/env-ci.json`

---

## Estructura

```
├── src/
│   ├── main/java/.../           ← Código de producción (heredado post1)
│   └── test/java/.../
│       ├── controller/          ← @WebMvcTest (heredados)
│       ├── repository/          ← @DataJpaTest (heredados)
│       ├── service/             ← Mockito (heredados)
│       └── e2e/
│           ├── TareasPage.java       ← Page Object lista
│           ├── NuevaTareaPage.java   ← Page Object formulario
│           └── TareasE2ETest.java    ← Tests Selenium headless
├── postman/
│   ├── ColeccionToDo.json       ← Colección Postman con 5 requests
│   ├── env-local.json           ← Entorno local (baseUrl=localhost:8080)
│   └── env-ci.json              ← Entorno CI (mismo baseUrl para Actions)
└── .github/
    └── workflows/
        └── api-tests.yml        ← Pipeline Newman en GitHub Actions
```

---

## Ejecución

### Tests unitarios (sin app corriendo)
```bash
mvn test
```

### Tests E2E con Selenium (requiere app corriendo)
```bash
# Terminal 1: iniciar la app
mvn spring-boot:run

# Terminal 2: ejecutar solo los tests E2E
mvn test -Dtest=TareasE2ETest -DfailIfNoTests=false
```

### Newman local (requiere app corriendo y Node.js 18+)
```bash
# Instalar Newman
npm install -g newman

# Terminal 1: iniciar la app
mvn spring-boot:run

# Terminal 2: ejecutar la colección
newman run postman/ColeccionToDo.json --environment postman/env-local.json
```

### GitHub Actions
El workflow se activa automáticamente al hacer `push` al repositorio. Ver resultados en la pestaña **Actions** del repositorio GitHub.

---

## Historial de commits

1. `feat: Page Objects TareasPage y NuevaTareaPage con patron POM Selenium`
2. `feat: coleccion Postman 5 requests con test scripts y entornos local y ci`
3. `feat: workflow GitHub Actions Newman pipeline y TareasE2ETest headless`
