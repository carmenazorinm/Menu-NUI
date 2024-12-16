using System;
using Leap;
using Unity.VisualScripting;
using UnityEditorInternal;
using UnityEngine;
using UnityEngine.InputSystem.Controls;

public class Node {

    private GameObject sphere;
    private Vector3 position;
    private bool isSelected = false;
    private bool isPathMember = false;
    private Renderer renderer;

    public Node(Vector3 position){
        this.position = position;

        this.sphere = GameObject.CreatePrimitive(PrimitiveType.Sphere);
        this.sphere.transform.position = this.position;
        sphere.transform.localScale = Vector3.one * 0.5f;

        this.renderer = sphere.GetComponent<Renderer>();
    }

    public GameObject getSphere(){
        return sphere;
    }

    public Vector3 getPosition(){
        return this.position;
    }

    public void setIsSelected(bool b){
        this.isSelected = b;
        setColor();
    }

    public bool getSelected(){
        return this.isSelected;
    }

    public void setIsPathMember(bool b){
        this.isPathMember = b;
        setColor();
    }

    public bool getIsPathMember(){
        return this.isPathMember;
    }

    public void setColor(){
        if(this.isSelected){
            this.renderer.material.color = new Color(255, 0, 0);
        }else if(this.isPathMember){
            this.renderer.material.color = new Color(100, 0, 100);
        }
        else{
            this.renderer.material.color = new Color(255, 255, 255);
        }
    }
    public bool Equals(Node node){
        return node.position[0] == this.position[0] 
        && node.position[1] == this.position[1]
        && node.position[2] == this.position[2];
    }

}