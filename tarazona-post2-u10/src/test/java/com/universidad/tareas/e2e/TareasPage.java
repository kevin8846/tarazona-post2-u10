package com.universidad.tareas.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

/**
 * Page Object: encapsula los selectores y acciones de la página
 * principal de tareas (http://localhost:8080/tareas).
 *
 * Patrón Page Object Model (POM): los tests NO conocen los selectores,
 * solo llaman a métodos de esta clase.
 *
 * @author Andres Felipe Jimenez Ramirez
 * Programación Web - Unidad 10 - Post-Contenido 2
 */
public class TareasPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Selectores encapsulados como constantes privadas ──
    private static final By TITULO_PAGINA    = By.tagName("h1");
    private static final By LISTA_TAREAS     = By.cssSelector(".tarea-item");
    private static final By BTN_NUEVA        = By.id("btn-nueva");
    private static final By CAMPO_TITULO     = By.id("titulo");
    private static final By CAMPO_DESCRIPCION= By.id("descripcion");
    private static final By BTN_GUARDAR      = By.id("btn-guardar");
    private static final By MENSAJE_EXITO    = By.cssSelector(".alert-success");
    private static final By MENSAJE_ERROR    = By.cssSelector(".alert-danger");

    public TareasPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /** Retorna el título H1 visible en la página */
    public String obtenerTituloPagina() {
        return driver.findElement(TITULO_PAGINA).getText();
    }

    /** Retorna el título del documento (tag <title>) */
    public String obtenerTituloDocumento() {
        return driver.getTitle();
    }

    /** Cuenta cuántas tareas aparecen en la lista */
    public int contarTareas() {
        List<WebElement> items = driver.findElements(LISTA_TAREAS);
        return items.size();
    }

    /** Navega al formulario de nueva tarea */
    public NuevaTareaPage irANuevaTarea() {
        driver.findElement(BTN_NUEVA).click();
        return new NuevaTareaPage(driver);
    }

    /** Verifica si la página está cargada correctamente */
    public boolean estaCargada() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(TITULO_PAGINA));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Retorna la URL actual */
    public String obtenerUrl() {
        return driver.getCurrentUrl();
    }
}
