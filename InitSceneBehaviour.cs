using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Leap;
using UnityEngine.SceneManagement;

public class InitSceneBehaviour : MonoBehaviour
{
    // Start is called before the first frame update
    public LeapServiceProvider leapProvider;
    void Start()
    {
        Language.isEnglish = false;
    }

    // Update is called once per frame
    void Update()
    {
        Frame frame = leapProvider.CurrentFrame;
        if(frame.Hands.Count > 0){
            SceneManager.LoadScene("Main");
        }
    }
}
