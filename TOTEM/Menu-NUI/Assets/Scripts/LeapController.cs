using UnityEngine;
using Leap;
using Leap.Unity;

public class LeapMotionMenuController : MonoBehaviour
{
    private Controller controller;

    void Start()
    {
        // Inicializar el controlador de Leap Motion
        controller = new Controller();
    }

    void Update()
    {
        // Obtener el frame actual del Leap Motion
        Frame frame = controller.Frame();
        
        // Comprobar si hay manos detectadas
        if (frame.Hands.Count > 0)
        {
            Hand hand = frame.Hands[0]; // Obtener la primera mano detectada

            // Detectar scroll
            DetectScroll(hand);
            
            // Detectar click
            DetectPinchGesture(hand);
        }
    }

    private void DetectScroll(Hand hand)
    {
        Vector handVelocity = hand.PalmVelocity;

        // Ajustar umbral de desplazamiento
        if (Mathf.Abs(handVelocity.y) > 300)
        {
            float scrollAmount = handVelocity.y * Time.deltaTime * 0.1f; // Ajustar la velocidad de scroll
            Scroll(scrollAmount);
        }
    }

    private void Scroll(float amount)
    {
        // Implementar la lógica de scroll, por ejemplo, desplazando un ScrollRect
        Debug.Log("Scrolling: " + amount);
        
        // Aquí podrías llamar a un método de desplazamiento de un ScrollRect
        // scrollRect.verticalNormalizedPosition += amount; // Asegúrate de ajustar esto a tu ScrollRect
    }

    private void DetectPinchGesture(Hand hand)
    {
        if (hand.PinchStrength > 0.8f) // Detectar un pellizco fuerte
        {
            // Lógica para el click
            Debug.Log("Pinch detected, performing click!");
            PerformClick();
        }
    }

    private void PerformClick()
    {
        // Aquí puedes activar un botón o hacer clic en un elemento de UI
        // Por ejemplo, utilizando un botón de Unity:
        // Button myButton = GetComponent<Button>();
        // myButton.onClick.Invoke();
        Debug.Log("Button clicked!");
    }
}

