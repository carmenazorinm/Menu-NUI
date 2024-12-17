using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;
using Leap;
using UnityEditor.Experimental.GraphView;
using System;
using UnityEngine.Events;
using UnityEngine.SceneManagement;

public class MainSceneBahaviourr : MonoBehaviour
{
    public UIDocument root;
    public PinchDetector scroller;
    public PinchDetector clicker;
    private GroupBox menu;
    private Button[] buttons = new Button[8];
    private UnityEvent[] buttonEvents = new UnityEvent[8];
    private Hand hand;
    private const int NUM_BUTTONS = 8;

    // Start is called before the first frame update
    void Start()
    {
        menu = root.rootVisualElement.Q<GroupBox>("Canvas");
        buttons[0] = root.rootVisualElement.Q<Button>("Docencia");
        buttons[1] = root.rootVisualElement.Q<Button>("gdt");
        buttons[2] = root.rootVisualElement.Q<Button>("Comedor");
        buttons[3] = root.rootVisualElement.Q<Button>("Profesorado");
        buttons[4] = root.rootVisualElement.Q<Button>("Aula");
        buttons[5] = root.rootVisualElement.Q<Button>("ec");
        buttons[6] = root.rootVisualElement.Q<Button>("se");
        buttons[7] = root.rootVisualElement.Q<Button>("esp_eng");

        for(int i = 0; i < NUM_BUTTONS; i++){
            buttonEvents[i] = new UnityEvent();
        }
        buttonEvents[0].AddListener(loadTeachingScene);
        buttonEvents[0].AddListener(loadProcedureManagmentScene);
        buttonEvents[0].AddListener(loadDiningHallScene);
        buttonEvents[0].AddListener(loadAcademicStaffScene);
        buttonEvents[0].AddListener(loadRoomsScene);
        buttonEvents[0].AddListener(loadPublicSpacesScene);
        buttonEvents[0].AddListener(loadExternalServicesScene);
        buttonEvents[7].AddListener(changeLanguage);

        if(Language.isEnglish){
            for(int i = 0; i < NUM_BUTTONS; i++){
                buttons[i].name = Language.ENGLISH_BUTTON_NAMES[i];
            }
        }
    }

    private void Update()
    {
        
        if (scroller.IsPinching)
        {
            scroller.TryGetHand(out hand);
            // Handle pinch logic here
            Debug.Log("Scroll with squish percent: " + scroller.SquishPercent);
            if(hand != null){
                Vector3 handVelocity = hand.PalmVelocity;
                Debug.Log(handVelocity);

                float scrollAmount =  handVelocity.y*10; // Ajustar la velocidad de scroll
                Vector3 original = menu.transform.position;
                Vector3 scroll = new Vector3(0.0f, scrollAmount, 0.0f);
                menu.transform.position = original - scroll;
                
            }
        }
        
        if (scroller.PinchStartedThisFrame)
        {

            // Handle pinch start logic here
            Debug.Log("Scroll started this frame.");
        }

        if (clicker.IsPinching){
            scroller.TryGetHand(out hand);
            Finger middle_finger = hand.Middle;
            Vector3 finger_tip = middle_finger.TipPosition;
            Vector2 pos = new Vector2(finger_tip[0], finger_tip[1]);
            
            


            for(int i = 0; i < NUM_BUTTONS; i++){
                if(buttons[i].worldBound.Contains(pos)){
                    Debug.Log(buttons[i].name + " was pressed");
                    buttonEvents[i].Invoke();
                }
            }
            
            Debug.Log("Click with squish percent: " + clicker.SquishPercent);
        }

        if (clicker.PinchStartedThisFrame)
        {
            // Handle pinch start logic here
            Debug.Log("Click started this frame.");
        }
    }

    private void changeLanguage(){
        if(Language.isEnglish){
            for(int i = 0; i < NUM_BUTTONS; i++){
                buttons[i].name = Language.SPANISH_BUTTON_NAMES[i];
            }
            Language.isEnglish=false;
        }
        else{
            for(int i = 0; i < NUM_BUTTONS; i++){
                buttons[i].name = Language.ENGLISH_BUTTON_NAMES[i];
            }
            Language.isEnglish=false;
        }
    }

    private void loadTeachingScene(){
        //SceneManager.LoadScene("Scene");
    }
    private void loadProcedureManagmentScene(){
        //SceneManager.LoadScene("Scene");
    }
    private void loadDiningHallScene(){
        //SceneManager.LoadScene("Scene");
    }
    private void loadAcademicStaffScene(){
        //SceneManager.LoadScene("Scene");
    }
    private void loadRoomsScene(){
        //SceneManager.LoadScene("Scene");
    }
    private void loadPublicSpacesScene(){
        //SceneManager.LoadScene("Scene");
    }
    private void loadExternalServicesScene(){
        //SceneManager.LoadScene("Scene");
    }

}
