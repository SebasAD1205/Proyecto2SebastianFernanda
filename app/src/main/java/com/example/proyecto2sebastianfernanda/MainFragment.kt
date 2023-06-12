package com.example.proyecto2sebastianfernanda

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



class MainFragment : Fragment() {
    private lateinit var etNombre: EditText
    private lateinit var iVPersonaje: ImageView
    private lateinit var btnJugar: Button
    private lateinit var tVBestScore: TextView

    private lateinit var mp: MediaPlayer
    private var numAleatorio: Int = (Math.random() * 10).toInt()

    @SuppressLint("SetTextI18n", "DiscouragedApi")
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

        val bd = FirebaseFirestore.getInstance()

        bd.collection("Jugadores")
            .document("3pIGRRf1se02EB8y6AvJ")
            .get().addOnSuccessListener {
                val tempNombre = it.get("nombre")
                val tempScore = it.get("score")
                tVBestScore.text = "Record: $tempScore de $tempNombre"
            }

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

    override fun onStart() {
        super.onStart()
        mp = MediaPlayer.create(requireContext(), R.raw.alphabet_song)
        mp.isLooping = true
        mp.start()
    }

    override fun onStop() {
        super.onStop()
        mp.stop()
        mp.release()
    }
    private fun jugar() {
        val nombre = etNombre.text.toString()

        if (nombre.isNotEmpty()) {
            val fragment = NivelBaseFragment()
            val bundle = Bundle()
            bundle.putString("jugador", nombre)
            fragment.arguments = bundle


            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            etNombre.setText("")
        } else {
            Toast.makeText(requireContext(), "Debe escribir su nombre!", Toast.LENGTH_SHORT).show()
            etNombre.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etNombre, InputMethodManager.SHOW_IMPLICIT)
        }
    }

}
