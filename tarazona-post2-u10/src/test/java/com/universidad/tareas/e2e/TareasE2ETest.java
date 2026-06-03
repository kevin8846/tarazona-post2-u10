package com.universidad.tareas.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas E2E con Selenium WebDriver aplicando el patrón Page Object Model.
 * Ejecuta Chrome en modo headless (sin interfaz gráfica).
 *
 * IMPORTANTE: Requiere que la app esté corriendo en localhost:8080.
 * Excluido del ciclo mvn test normal — ejecutar manualmente o via CI.
 *
 * Patrón de nomenclatura: metodo_condicion_resultadoEsperado
 *
 * @author Andres Felipe Jimenez Ramirez
 * Programación Web - Unidad 10 - Post-Contenido 2
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TareasE2ETest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        // WebDriverManager descarga ChromeDriver automáticamente
        WebDriverManager.chromedriver().setup();

        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless");          // sin ventana gráfica
        opts.addArguments("--no-sandbox");        // requerido en CI/Linux
        opts.addArguments("--disable-dev-shm-usage");
        opts.addArguments("--disable-gpu");
        opts.addArguments("--window-size=1280,720");

        driver = new ChromeDriver(opts);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Test 1: La página de tareas carga correctamente ──────────────────────

    @Test
    @Order(1)
    @DisplayName("paginaTareas_cargaUrl_retornaTituloEsperado")
    void paginaTareas_cargaUrl_retornaTituloEsperado() {
        driver.get(BASE_URL + "/tareas");
        TareasPage page = new TareasPage(driver);

        assertThat(page.estaCargada()).isTrue();
        // El título del documento debe contener "Tareas"
        assertThat(driver.getTitle()).containsIgnoringCase("Tareas");
    }

    // ── Test 2: La URL de la página es correcta ───────────────────────────────

    @Test
    @Order(2)
    @DisplayName("paginaTareas_navegarUrl_urlContieneRutaEsperada")
    void paginaTareas_navegarUrl_urlContieneRutaEsperada() {
        driver.get(BASE_URL + "/tareas");
        TareasPage page = new TareasPage(driver);

        assertThat(page.obtenerUrl()).contains("/tareas");
    }

    // ── Test 3: La API responde con 200 en /api/tareas ────────────────────────

    @Test
    @Order(3)
    @DisplayName("apiTareas_getEndpoint_paginaCargaSinErrores")
    void apiTareas_getEndpoint_paginaCargaSinErrores() {
        driver.get(BASE_URL + "/api/tareas");

        // La respuesta JSON no debe contener error
        String source = driver.getPageSource();
        assertThat(source).doesNotContain("Whitelabel Error Page");
    }

    // ── Test 4: Endpoint de salud de la aplicación responde ──────────────────

    @Test
    @Order(4)
    @DisplayName("actuatorHealth_endpoint_retornaStatusUp")
    void actuatorHealth_endpoint_retornaStatusUp() {
        driver.get(BASE_URL + "/actuator/health");

        String source = driver.getPageSource();
        assertThat(source).contains("UP");
    }
}
