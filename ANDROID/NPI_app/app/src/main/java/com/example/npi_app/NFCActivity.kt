package com.example.npi_app


import android.app.Activity
import android.content.Intent
import android.widget.TextView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import android.app.PendingIntent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.SpannableString
import androidx.annotation.RequiresApi
import org.json.JSONObject

import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale


class NFCActivity : BaseActivity(), TextToSpeech.OnInitListener{
    private var nfcAdapter: NfcAdapter? = null
    private var textToSpeech: TextToSpeech? = null

    // IDs of the chips that are allowed to access the system
    private val allowedIds = arrayOf(
        "BD:86:7E:3E",
        "ED:9E:F3:ED",
        "02:59:26:35:04:A0:00",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nfc_loading_layout)

        loadTimetable()

        // Initialize NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Log.e("NFC", "NFC is not available on this device.")
            finish() // Close the activity if NFC is not supported
            return
        }

        //Initialize TTS
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onResume() {
        super.onResume()
        // Set up NFC foreground dispatch
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        // Disable NFC foreground dispatch
        disableForegroundDispatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    private fun enableForegroundDispatch() {
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )

        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))

        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    private fun disableForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Handle NFC intent
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            // Aquí ya puedes continuar con el código, sabiendo que `intent` no es null
            val nfcScanImg = findViewById<ImageView>(R.id.nfcScanImg)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)

            if (nfcScanImg != null && progressBar != null) {
                nfcScanImg.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val extra_ndef = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            Log.d("NFC", "NDEF: $extra_ndef")

            tag?.let {
                val id = it.id
                val hexId = id.joinToString(separator = ":") { byte -> String.format("%02X", byte) }
                Log.d("NFC", "NFC Tag ID: $hexId")

                val techList = it.techList
                Log.d("NFC", "Tech list: ${techList.joinToString()}")

                // Check if the ID is allowed

                // Access granted
                Toast.makeText(this, "NFC device detected", Toast.LENGTH_SHORT).show()

                setContentView(R.layout.nfc_loaded_classroom_layout)

                processNfcTag(hexId)
            }
        }
    }


    override fun onInit(p0: Int) {
        if(p0 == TextToSpeech.SUCCESS){
            val result = textToSpeech?.setLanguage(Locale("es", "ES"))

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "El idioma no es compatible")
            }
        }
        else{
            Log.e("TTS", "La inicialización falló")
        }
    }

    data class Classroom(
        val name: String,
        val timetable: Map<String, List<ClassSchedule>>
    )

    data class ClassSchedule(
        val subject: String,
        val start: String,
        val end: String,
        val group: String
    )

    data class Timetable(
        val classrooms:Map<String, Classroom>,
        val personalTimetable: Map<String, List<ClassSchedule>>
    )

    private lateinit var timetable: Timetable

    private lateinit var classroomTimetables: Map<String, Map<String, Any>>
    private lateinit var personalTimetable: Map<String, List<Map<String, String>>>

    private fun loadTimetable() {
        val inputStream = resources.openRawResource(R.raw.timetable)
        val jsonText = inputStream.bufferedReader().use { it.readText() }

        val jsonObject = JSONObject(jsonText)

        classroomTimetables = jsonObject.getJSONObject("classrooms").keys().asSequence().associateWith { key ->
            val classroom = jsonObject.getJSONObject("classrooms").getJSONObject(key)
            mapOf(
                "name" to classroom.getString("name"),
                "timetable" to classroom.getJSONObject("timetable").keys().asSequence().associateWith { dayKey ->
                    classroom.getJSONObject("timetable").getJSONArray(dayKey).let { scheduleArray ->
                        (0 until scheduleArray.length()).map { index ->
                            val schedule = scheduleArray.getJSONObject(index)
                            mapOf(
                                "subject" to schedule.getString("subject"),
                                "start" to schedule.getString("start"),
                                "end" to schedule.getString("end"),
                                "group" to schedule.getString("group"),
                                "room" to schedule.getString("room")
                            )
                        }
                    }
                }
            )
        }

        personalTimetable = jsonObject.getJSONObject("personalTimetable").keys().asSequence().associateWith { dayKey ->
            jsonObject.getJSONObject("personalTimetable").getJSONArray(dayKey).let { scheduleArray ->
                (0 until scheduleArray.length()).map { index ->
                    val schedule = scheduleArray.getJSONObject(index)
                    mapOf(
                        "subject" to schedule.getString("subject"),
                        "start" to schedule.getString("start"),
                        "end" to schedule.getString("end"),
                        "group" to schedule.getString("group"),
                        "room" to schedule.getString("room")
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processNfcTag(tagId: String) {
        val classroom = classroomTimetables[tagId]
        if (classroom != null) {
            val name = classroom["name"] as String
            val timetable = classroom["timetable"] as Map<String, List<Map<String, String>>>

            val today = LocalDate.now().dayOfWeek
            val todaysClasses = timetable[today.name] ?: listOf()

            val now = LocalTime.now()

            val currentClass = todaysClasses.find {
                val start = LocalTime.parse(it["start"])
                val end = LocalTime.parse(it["end"])
                now.isAfter(start) && now.isBefore(end)
            }

            val nextClass = todaysClasses.find {
                val start = LocalTime.parse(it["start"])
                now.isBefore(start)
            }

            val currentClassMessage = if (currentClass != null) {
                getString(R.string.classroom_info_room, name) + " " + getString(R.string.classroom_info_curr_subject, currentClass["subject"]!!, currentClass["group"]!!)
                //"Estás en el aula $name. Ahora mismo en este aula tiene lugar la clase de ${currentClass["subject"]}."
                //"Currently, ${currentClass["subject"]} is ongoing in classroom $name."
            } else {
                getString(R.string.classroom_info_room, name) + " " + getString(R.string.classroom_info_curr_no_subject)
                //"Estás en el aula $name. Ahora mismo no hay ninguna clase en este aula."
                //"There is no class currently ongoing in classroom $name."
            }

            val nextClassMessage = if (nextClass != null) {
                getString(R.string.classroom_info_next_subject, nextClass["subject"]!!, nextClass["group"], nextClass["start"]!!)
                //"La próxima clase es de ${nextClass["subject"]} y comienza a las ${nextClass["start"]}."
                //"The next class is ${nextClass["subject"]} starting at ${nextClass["start"]}."
            } else {
                getString(R.string.classroom_info_next_no_subject)
                //"No hay más clases programadas en el aula $name para hoy."
                //"There are no more classes scheduled in classroom $name for today."
            }

            val studentClassesToday = personalTimetable[today.name] ?: listOf()
            val isStudentInClass = currentClass != null && studentClassesToday.any {
                it["subject"] == currentClass["subject"] && it["group"] == currentClass["group"] &&
                        now.isAfter(LocalTime.parse(it["start"])) &&
                        now.isBefore(LocalTime.parse(it["end"]))
            }
            val currentStudentClass = studentClassesToday.find {
                val start = LocalTime.parse(it["start"])
                val end = LocalTime.parse(it["end"])
                now.isAfter(start) && now.isBefore(end)
            }
            val nextStudentClass = studentClassesToday.find {
                val start = LocalTime.parse(it["start"])
                now.isBefore(start)
            }

            val studentClassMessage = if (isStudentInClass) {
                getString(R.string.classroom_info_this_room_yes)
                //"Tienes clase en este aula en este momento."
                //"You have class right now in this classroom."
            } else {
                // If the student has currently class, add to the message, otherwise, add they have no class
                getString(R.string.classroom_info_this_room_no) + " " + if (currentStudentClass != null) {
                    getString(R.string.classroom_info_my_curr_subject, currentStudentClass["room"], currentStudentClass["subject"]!!)
                }
                else{
                    getString(R.string.classroom_info_my_curr_subject_no)
                }
                //"No tienes clase en este aula en este momento."
                //"You do not have class in this classroom at the moment."
            }

            val nextStudentClassMessage = if (nextStudentClass != null) {
                getString(R.string.classroom_info_my_next_subject, nextStudentClass["subject"]!!, nextStudentClass["room"], nextStudentClass["start"]!!)
                //"Tu próxima clase es de ${nextStudentClass["subject"]} y comienza a las ${nextStudentClass["start"]}."
                //"Your next class is ${nextStudentClass["subject"]} starting at ${nextStudentClass["start"]}."
            } else {
                getString(R.string.classroom_info_my_next_subject_no)
                //"No tienes más clases programadas en este aula para hoy."
                //"You have no more classes scheduled in this classroom for today."
            }

            val message = "$currentClassMessage $nextClassMessage $studentClassMessage $nextStudentClassMessage"
            val html_message = Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT)

            val short_msg_room = getString(R.string.classroom_short_room, name)
            val short_msg_curr_subj = if (currentClass != null) {
                getString(R.string.classroom_short_curr_subject, currentClass["subject"]!!, currentClass["group"]!!)
            } else {
                "-"
            }
            val short_msg_next_subj = if (nextClass != null) {
                getString(R.string.classroom_short_next_subject, nextClass["subject"]!!, nextClass["group"]!!, nextClass["start"]!!)
            } else {
                "-"
            }
            val short_msg_my_curr_subj = if (currentStudentClass != null) {
                getString(R.string.classroom_short_my_curr_subject, currentStudentClass["subject"]!!, currentStudentClass["group"]!!, currentStudentClass["room"]!!)
            } else {
                "-"
            }
            val short_msg_my_next_subj = if (nextStudentClass != null) {
                getString(R.string.classroom_short_my_next_subject, nextStudentClass["subject"]!!, nextStudentClass["group"]!!, nextStudentClass["start"]!!, nextStudentClass["room"]!!)
            } else {
                "-"
            }

            val short_msg = "$short_msg_room<br/><br/>$short_msg_curr_subj<br/><br/>$short_msg_next_subj<br/><br/><br/><br/><br/>$short_msg_my_curr_subj<br/><br/>$short_msg_my_next_subj"
            val short_html_msg = Html.fromHtml(short_msg, Html.FROM_HTML_MODE_COMPACT)

            textToSpeech?.speak(html_message, TextToSpeech.QUEUE_FLUSH, null, null)
            findViewById<TextView>(R.id.class_info_txt).text = short_html_msg
        } else {
            val short_msg_room = getString(R.string.classroom_short_room, "???")
            val short_msg = "$short_msg_room<br/><br/>"
            val short_html_msg = Html.fromHtml(short_msg, Html.FROM_HTML_MODE_COMPACT)

            findViewById<TextView>(R.id.class_info_txt).text = short_html_msg

            textToSpeech?.speak("Lo siento, no he reconocido este aula. ¿Seguro que lo has escaneado correctamente?", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

}

