using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;


public class Menu : MonoBehaviour
{
    // Función para cargar la escena de Docencia
    public void OnDocenciaButton()
    {
        SceneManager.LoadScene(1); // Cargar escena de Docencia
    }

    // Función para cargar la escena de Gestión de trámites
    public void OnTramitesButton()
    {
        SceneManager.LoadScene(2); // Cargar escena de Gestión de trámites
    }

    // Función para cargar la escena de Comedor
    public void OnComedorButton()
    {
        SceneManager.LoadScene(3); // Cargar escena de Comedor
    }

    // Función para cargar la escena de Profesorado
    public void OnProfesoradoButton()
    {
        SceneManager.LoadScene(4); // Cargar escena de Profesorado
    }

    // Función para cargar la escena de Aula
    public void OnAulaButton()
    {
        SceneManager.LoadScene(5); // Cargar escena de Aula
    }

    // Función para cargar la escena de Espacios comunes
    public void OnEspaciosComunesButton()
    {
        SceneManager.LoadScene(6); // Cargar escena de Espacios comunes
    }

    // Función para cargar la escena de Servicios externos
    public void OnServiciosExternosButton()
    {
        SceneManager.LoadScene(7); // Cargar escena de Servicios externos
    }

    // Función para cargar la escena de Otras
    public void OnOtrasButton()
    {
        SceneManager.LoadScene(8); // Cargar escena de Otras
    }

    // Función para cargar el menú principal
    public void OnBackToMenuButton()
    {
        SceneManager.LoadScene(0); 
        Debug.Log("Botón Volver presionado");
    }

    // Función para salir del juego
    public void OnQuitButton()
    {
        Application.Quit(); // Cerrar el juego
    }
}
