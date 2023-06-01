package com.example.proyecto2sebastianfernanda

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private lateinit var etNombre: EditText
    private lateinit var iVPersonaje: ImageView
    private lateinit var btnJugar: Button
    private lateinit var tVBestScore: TextView

    private lateinit var mp: MediaPlayer
    private var numAleatorio: Int = (Math.random() * 10).toInt()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        etNombre = rootView.findViewById(R.id.etNombre)
        iVPersonaje = rootView.findViewById(R.id.iVPersonaje)
        btnJugar = rootView.findViewById(R.id.btnJugar)
        tVBestScore = rootView.findViewById(R.id.tVBestScore)

        btnJugar.setOnClickListener { jugar() }

        val admin = AdminSQLiteOpenHelper(requireContext(), "BD", null, 1)
        val db = admin.writableDatabase

        val consulta = db.rawQuery(
            "select * from puntaje where score = (select max(score) from puntaje)", null
        )

        if (consulta.moveToFirst()) {
            val temp_nombre = consulta.getString(0)
            val temp_score = consulta.getString(1)

            tVBestScore.text = "Record: $temp_score de $temp_nombre"
        }

        mp = MediaPlayer.create(requireContext(), R.raw.alphabet_song)
        mp.start()
        mp.isLooping = true

        val id = when (numAleatorio) {
            0, 10 -> resources.getIdentifier("mango", "drawable", requireContext().packageName)
            1, 9 -> resources.getIdentifier("fresa", "drawable", requireContext().packageName)
            2, 8 -> resources.getIdentifier("manzana", "drawable", requireContext().packageName)
            3, 7 -> resources.getIdentifier("sandia", "drawable", requireContext().packageName)
            else -> resources.getIdentifier("naranja", "drawable", requireContext().packageName)
        }
        iVPersonaje.setImageResource(id)

        return rootView
    }

    private fun jugar() {
        val nombre = etNombre.text.toString()

        if (nombre.isNotEmpty()) {
            mp.stop()
            mp.release()

            val intent = Intent(requireContext(), NivelBaseFragment::class.java)
            intent.putExtra("jugador", nombre)
            startActivity(intent)
            requireActivity().finish()
        } else {
            Toast.makeText(requireContext(), "Debe escribir su nombre!", Toast.LENGTH_SHORT).show()

            etNombre.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etNombre, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun onBackPressed() {

    }
}
