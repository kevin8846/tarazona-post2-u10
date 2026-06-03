package com.universidad.tareas.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object: encapsula los selectores y acciones del formulario
 * de creación de nueva tarea.
 *
 * @author Andres Felipe Jimenez Ramirez
 */
public class NuevaTareaPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Selectores encapsulados como constantes privadas ──
    private static final By CAMPO_TITULO      = By.id("titulo");
    private static final By CAMPO_DESCRIPCION = By.id("descripcion");
    private static final By BTN_GUARDAR       = By.id("btn-guardar");
    private static final By BTN_CANCELAR      = By.id("btn-cancelar");
    private static final By MENSAJE_ERROR     = By.cssSelector(".field-error");

    public NuevaTareaPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /** Ingresa el título de la tarea */
    public NuevaTareaPage ingresarTitulo(String titulo) {
        WebElement campo = driver.findElement(CAMPO_TITULO);
        campo.clear();
        campo.sendKeys(titulo);
        return this;
    }

    /** Ingresa la descripción */
    public NuevaTareaPage ingresarDescripcion(String descripcion) {
        WebElement campo = driver.findElement(CAMPO_DESCRIPCION);
        campo.clear();
        campo.sendKeys(descripcion);
        return this;
    }

    /** Hace clic en Guardar y retorna a la página de lista */
    public TareasPage guardar() {
        driver.findElement(BTN_GUARDAR).click();
        return new TareasPage(driver);
    }

    /** Hace clic en Cancelar */
    public TareasPage cancelar() {
        driver.findElement(BTN_CANCELAR).click();
        return new TareasPage(driver);
    }

    /** Verifica si aparece mensaje de error de validación */
    public boolean tieneErrorDeValidacion() {
        return !driver.findElements(MENSAJE_ERROR).isEmpty();
    }

    /** Retorna la URL actual */
    public String obtenerUrl() {
        return driver.getCurrentUrl();
    }
}
