package com.example.npi_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfesoradoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profesorado)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Lista de ejemplo
        val profesores = listOf(
            Profesor("Marcelino José Cabrera Cuevas", "Departamento de Lenguajes y Sistemas Informáticos", "Miércoles\t11:00 - 14:00\tD2F (Traducción e Interpretación)\nJueves\t9:00 - 10:30\tD25\n" +
                    "Jueves\t12:30 - 14:00\tD25", "Lunes\t10:30 - 13:30\tD25\nMartes\t10:30 - 13:30\tD25"),
            Profesor("Francisco Javier Abad Ortega", "Profesor Contratado Doctor Indefinido", "Miércoles\t9:30 - 13:30\tD68-4P\nMiércoles\t18:30 - 20:30\tD68-4P", "Jueves\t9:30 - 12:30\tD20"),
            Profesor("Silvia Acid Carrillo", "Profesora Titular de Universidad", "Lunes\t10:00 - 12:00\tD32\nJueves\t15:00 - 17:00\tD32", "Miércoles\t11:00 - 13:00\tD22"),
            Profesor("Eugenio Aguirre Molina", "Profesor Titular de Universidad", "Martes\t9:30 - 11:30\tD45", "Viernes\t16:00 - 18:00\tD20"),
            Profesor("Antonio Bautista Bailón Morillas", "Profesor Colaborador Indefinido", "Miércoles\t8:30 - 10:30\tD30", "Jueves\t14:00 - 16:00\tD40"),
            Profesor("José Manuel Benítez Sánchez", "Catedrático de Universidad", "Martes\t9:00 - 11:00\tD44\nViernes\t13:00 - 15:00\tD45", "Jueves\t12:00 - 14:00\tD21"),
            Profesor("Christoph Norbert Bergmeir", "Contratado María Zambrano Senior", "Jueves\t10:30 - 12:30\tD31", "Lunes\t11:00 - 13:00\tD25"),
            Profesor("Fernando Berzal Galiano", "Profesor Contratado Doctor Indefinido", "Viernes\t9:30 - 11:30\tD50\nMartes\t14:00 - 16:00\tD51", "Miércoles\t13:00 - 15:00\tD20"),
            Profesor("Ignacio José Blanco Medina", "Director de Departamento", "Lunes\t10:00 - 12:00\tD10", "Martes\t14:00 - 16:00\tD10"),
            Profesor("Francisco Javier Cabrerizo Lorite", "Secretario de Departamento", "Miércoles\t9:30 - 12:30\tD12", "Jueves\t13:00 - 15:00\tD14"),
            Profesor("Luis Miguel Campos Ibáñez", "Catedrático de Universidad", "Viernes\t8:00 - 10:00\tD16", "Martes\t15:00 - 17:00\tD16"),
            Profesor("Carlos Cano Gutiérrez", "Profesor Titular de Universidad", "Lunes\t9:00 - 11:00\tD20", "Miércoles\t13:00 - 15:00\tD25"),
            Profesor("José Enrique Cano Ocaña", "Profesor Titular de Universidad", "Martes\t10:00 - 12:00\tD30\nViernes\t11:00 - 13:00\tD30", "Jueves\t12:00 - 14:00\tD28"),
            Profesor("Andrés Cano Utrera", "Catedrático de Universidad", "Miércoles\t9:00 - 11:00\tD44", "Viernes\t14:00 - 16:00\tD21"),
            Profesor("Jorge Casillas Barranquero", "Catedrático de Universidad", "Lunes\t11:00 - 13:00\tD45", "Jueves\t9:30 - 11:30\tD44"),
            Profesor("Francisco Miguel Castro Macías", "Contratos Predoctorales Ley 14/2011 FPU", "Martes\t13:30 - 15:30\tD20", "Lunes\t10:00 - 12:00\tD31"),
            Profesor("Juan Luis Castro Peña", "Catedrático de Universidad", "Miércoles\t12:00 - 14:00\tD50", "Viernes\t10:00 - 12:00\tD51"),
            Profesor("Jesús Chamorro Martínez", "Catedrático de Universidad", "Martes\t11:00 - 13:00\tD32", "Lunes\t15:00 - 17:00\tD33"),
            Profesor("Manuel Jesús Cobo Martín", "Profesor Titular de Universidad", "Jueves\t9:00 - 11:00\tD40", "Martes\t14:00 - 16:00\tD45"),
            Profesor("Óscar Cordón García", "Catedrático de Universidad", "Lunes\t13:00 - 15:00\tD28", "Viernes\t9:30 - 11:30\tD25"),
            Profesor("Francisco José Cortijo Bon", "Profesor Titular de Universidad", "Miércoles\t10:30 - 12:30\tD26", "Jueves\t14:00 - 16:00\tD40"),
            Profesor("David Criado Ramón", "Profesor Sustituto (LOSU)", "Martes\t15:30 - 17:30\tD50", "Viernes\t10:00 - 12:00\tD52"),
            Profesor("Carlos Alberto Cruz Corona", "Profesor Titular de Universidad", "Jueves\t11:00 - 13:00\tD35", "Lunes\t14:00 - 16:00\tD22"),
            Profesor("Juan Carlos Cubero Talavera", "Catedrático de Universidad", "Viernes\t8:30 - 10:30\tD40", "Martes\t11:00 - 13:00\tD50"),
            Profesor("Natalia Ana Díaz Rodríguez", "Profesora Titular de Universidad", "Lunes\t14:30 - 16:30\tD45", "Jueves\t10:00 - 12:00\tD50"),
            Profesor("Juan Manuel Fernández Luna", "Catedrático de Universidad", "Martes\t9:00 - 11:00\tD28\nMiércoles\t12:00 - 14:00\tD29", "Viernes\t15:00 - 17:00\tD32"),
            Profesor("Juan Fernández Olivares", "Profesor Titular de Universidad", "Miércoles\t11:30 - 13:30\tD21\nViernes\t8:30 - 10:30\tD22", "Lunes\t10:30 - 12:30\tD33"),
            Profesor("Joaquín Fernández Valdivia", "Catedrático de Universidad", "Martes\t10:30 - 12:30\tD44", "Jueves\t13:00 - 15:00\tD31"),
            Profesor("Miguel García Silvente", "Profesor Titular de Universidad", "Viernes\t9:00 - 11:00\tD45", "Lunes\t12:00 - 14:00\tD41"),
            Profesor("José Antonio García Soria", "Catedrático de Universidad", "Lunes\t11:00 - 13:00\tD50", "Jueves\t14:30 - 16:30\tD44"),
            Profesor("Antonio Garrido Carrillo", "Profesor Titular de Universidad", "Miércoles\t10:00 - 12:00\tD20", "Viernes\t15:30 - 17:30\tD21"),
            Profesor("Manuel Gómez Olmedo", "Catedrático de Universidad", "Martes\t11:00 - 13:00\tD28", "Jueves\t10:00 - 12:00\tD29"),
            Profesor("Antonio González Muñoz", "Catedrático de Universidad", "Miércoles\t13:30 - 15:30\tD30", "Lunes\t12:30 - 14:30\tD20"),
            Profesor("Juan Carlos González Quesada", "Contratos Predoctorales Formación Doctores Ley 14/2011 FPI", "Viernes\t10:00 - 12:00\tD32", "Martes\t15:00 - 17:00\tD33"),
            Profesor("Marina Hernández Bautista", "Profesora Sustituta (LOSU)", "Jueves\t11:30 - 13:30\tD40", "Lunes\t13:30 - 15:30\tD31"),
            Profesor("Francisco Herrera Triguero", "Catedrático de Universidad", "Martes\t9:00 - 11:00\tD20", "Miércoles\t10:30 - 12:30\tD50"),
            Profesor("Juan Francisco Huete Guadix", "Catedrático de Universidad", "Lunes\t11:00 - 13:00\tD21", "Viernes\t14:30 - 16:30\tD22"),
            Profesor("Marios Kountouris", "Personal Investigador de Proyectos Internacionales", "Jueves\t12:30 - 14:30\tD33", "Martes\t9:00 - 11:00\tD40"),
            Profesor("Antonio Gabriel López Herrera", "Profesor Titular de Universidad", "Lunes\t8:30 - 10:30\tD44", "Miércoles\t14:00 - 16:00\tD50"),
            Profesor("Manuel Lozano Márquez", "Catedrático de Universidad", "Martes\t11:00 - 13:00\tD22", "Viernes\t13:00 - 15:00\tD25"),
            Profesor("Julián Luengo Martín", "Catedrático de Universidad", "Miércoles\t9:00 - 11:00\tD20\nJueves\t14:00 - 16:00\tD30", "Lunes\t13:00 - 15:00\tD50"),
            Profesor("Nicolás Marín Ruiz", "Catedrático de Universidad", "Martes\t10:00 - 12:00\tD33", "Viernes\t9:00 - 11:00\tD40"),
            Profesor("María José Martín Bautista", "Catedrática de Universidad", "Lunes\t15:00 - 17:00\tD21", "Jueves\t10:30 - 12:30\tD50"),
            Profesor("Javier Martínez Baena", "Profesor Titular de Universidad", "Miércoles\t11:00 - 13:00\tD44", "Lunes\t14:30 - 16:30\tD28")

        )

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProfesorAdapter(profesores)
    }
}

data class Profesor(
    val nombre: String,
    val departamento: String,
    val tutorias_primer_semestre: String = "",
    val tutorias_segundo_semestre: String = ""
)

class ProfesorAdapter(private val profesores: List<Profesor>) :
    RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder>() {

    class ProfesorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.nombre)
        val departamento: TextView = view.findViewById(R.id.departamento)
        val telefono: TextView = view.findViewById(R.id.telefono)
        val correo: TextView = view.findViewById(R.id.correo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profesor, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.nombre.text = profesor.nombre
        holder.departamento.text = profesor.departamento
        holder.telefono.text = profesor.tutorias_primer_semestre
        holder.correo.text = profesor.tutorias_segundo_semestre
    }

    override fun getItemCount(): Int {
        return profesores.size
    }
}
