using System;
using Unity.VisualScripting;
using UnityEditorInternal;
using UnityEngine;

public class Node {

    private GameObject sphere;
    private Vector3 position;
    private Boolean isElected;
    public Node(Vector3 position){
        this.position = position;

        sphere = GameObject.CreatePrimitive(PrimitiveType.Sphere);
        sphere.transform.position = this.position;
    }

    public GameObject getSphere(){
        return sphere;
    }

    public Vector3 getPosition(){
        return this.position;
    }

    public void setIsElected(Boolean b){
        this.isElected = b;
    }

    public Boolean getSelected(){
        return this.isElected;
    }

}