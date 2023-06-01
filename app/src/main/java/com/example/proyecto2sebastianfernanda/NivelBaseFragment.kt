package com.example.proyecto2sebastianfernanda


import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.proyecto2sebastianfernanda.MainFragment

class NivelBaseFragment : Fragment() {
    private lateinit var tv_nombre: TextView
    private lateinit var tv_score: TextView
    private lateinit var iv_Auno: ImageView
    private lateinit var iv_Ados: ImageView
    private lateinit var iv_Vidas: ImageView
    private lateinit var iv_Signo: ImageView
    private lateinit var et_Respuesta: EditText
    private lateinit var mp: MediaPlayer
    private lateinit var mpGreat: MediaPlayer
    private lateinit var mpBad: MediaPlayer
    private lateinit var btnComprobar: Button
    private var score = 0
    private var vidas = 3
    private var nivel = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_base_nivel, container, false)

        tv_nombre = view.findViewById(R.id.tv_nombre)
        tv_score = view.findViewById(R.id.tv_score)
        iv_Vidas = view.findViewById(R.id.iv_vidas)
        iv_Auno = view.findViewById(R.id.iv_Auno)
        iv_Ados = view.findViewById(R.id.iv_Ados)
        iv_Signo = view.findViewById(R.id.iv_Signo)
        et_Respuesta = view.findViewById(R.id.et_respuesta)
        btnComprobar = view.findViewById(R.id.btnComprobar)

        val nombre_jugador = arguments?.getString("jugador")
        tv_nombre.text = "Jugador: $nombre_jugador"

        val string_score = arguments?.getString("score")
        score = string_score?.toInt() ?: 0
        tv_score.text = "Score: $score"

        val string_vidas = arguments?.getString("vidas")
        vidas = string_vidas?.toInt() ?: 3
        updateVidasUI()

        mp = MediaPlayer.create(requireContext(), R.raw.alphabet_song)
        mp.start()
        mp.isLooping = true

        mpGreat = MediaPlayer.create(requireContext(), R.raw.wonderful)
        mpBad = MediaPlayer.create(requireContext(), R.raw.bad)

        numeroAleatorio()

        btnComprobar.setOnClickListener { comparar() }

        return view
    }

    private fun numeroAleatorio() {
        when (nivel) {
            1 -> {

            }
            2 -> {

            }
            3 -> {
                // Lógica para el nivel 3
                // ...
            }
            4 -> {

                // ...
            }
            else -> {

            }
        }
    }

    private fun getDrawableId(numero: Int): Int {
        return when (numero) {
            0 -> R.drawable.cero
            1 -> R.drawable.uno
            2 -> R.drawable.dos
            3 -> R.drawable.tres
            4 -> R.drawable.cuatro
            5 -> R.drawable.cinco
            6 -> R.drawable.seis
            7 -> R.drawable.siete
            8 -> R.drawable.ocho
            9 -> R.drawable.nueve
            else -> throw IllegalArgumentException("Número inválido: $numero")
        }
    }

    private fun baseDeDatos() {
        val admin = AdminSQLiteOpenHelper(requireContext(), "BD", null, 1)
        val db = admin.writableDatabase

        val values = ContentValues()
        values.put("nombre", "Jugador1")
        values.put("score", score)

        db.insert("puntaje", null, values)
        db.close()
    }

    private fun comparar() {
        val respuesta = et_Respuesta.text.toString()
        if (respuesta.isNotEmpty()) {
            val respuestaJugador = respuesta.toInt()

            var resultado = 0

            when (nivel) {
                1 -> {
                    // Lógica para el nivel 1
                    // resultado = // ...
                }
                2 -> {
                    // Lógica para el nivel 2
                    // resultado = // ...
                }
                3 -> {
                    // Lógica para el nivel 3
                    // resultado = // ...
                }
                4 -> {
                    // Lógica para el nivel 4
                    // resultado = // ...
                }
                else -> {
                    // Lógica para niveles adicionales
                }
            }

            if (respuestaJugador == resultado) {
                mpGreat.start()
                score++
                tv_score.text = "Score: $score"

                if (score == 10) { // Si el score alcanza 10
                    nivel++ // Incrementar el nivel
                    score = 0 // Reiniciar el score a 0
                    Toast.makeText(requireContext(), "¡Pasaste al siguiente nivel!", Toast.LENGTH_SHORT).show()
                }
            } else {
                mpBad.start()
                vidas--
                updateVidasUI()

                if (vidas == 0) { // Si se quedan sin vidas
                    Toast.makeText(requireContext(), "¡Perdiste todas tus vidas!", Toast.LENGTH_SHORT).show()
                    mp.stop()
                    mp.release()
                    val intent = Intent(requireContext(), MainFragment::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                baseDeDatos()
            }

            et_Respuesta.setText("")
            numeroAleatorio()
        } else {
            Toast.makeText(requireContext(), "Debes dar una respuesta", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateVidasUI() {
        when (vidas) {
            3 -> iv_Vidas.setImageResource(R.drawable.tresvidas)
            2 -> iv_Vidas.setImageResource(R.drawable.dosvidas)
            1 -> iv_Vidas.setImageResource(R.drawable.unavida)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mp.stop()
        mp.release()
        mpGreat.release()
        mpBad.release()
    }
}
