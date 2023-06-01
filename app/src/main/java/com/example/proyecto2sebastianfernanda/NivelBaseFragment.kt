package com.example.proyecto2sebastianfernanda


import android.content.ContentValues
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

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
    private var numeroAleatorioUno = -1
    private var numeroAleatorioDos = -1
    private lateinit var nombreJugador: String
    private var resultado = 0
    private var score = 0
    private var vidas = 3
    private var nivel = 8
    private var scoreLimite = 9
    private var tituloNivel="Sumas básicas"
    private var mensajeNivel = "Nivel $nivel - $tituloNivel"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_base_nivel, container, false)
        Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT).show()
        tv_nombre = view.findViewById(R.id.tv_nombre)
        tv_score = view.findViewById(R.id.tv_score)
        iv_Vidas = view.findViewById(R.id.iv_vidas)
        iv_Auno = view.findViewById(R.id.iv_Auno)
        iv_Ados = view.findViewById(R.id.iv_Ados)
        iv_Signo = view.findViewById(R.id.iv_Signo)
        et_Respuesta = view.findViewById(R.id.et_respuesta)
        btnComprobar = view.findViewById(R.id.btnComprobar)

        nombreJugador = arguments?.getString("jugador")!!
        tv_nombre.text = "Jugador: $nombreJugador"

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
        if (score <= scoreLimite) {
            when(nivel){
                1-> nivel1()
                2-> suma()
                3-> resta()
                4-> nivel4()
                5-> nivel5()
                6-> nivel6()
                7-> nivel7()
                8,9-> nivel8()
            }


        } else {
            if(nivel<=8) {
                scoreLimite += 10
                nivel++
            }
            else {
                scoreLimite = Int.MAX_VALUE
            }
            actualizarMensajeMetodo()
            Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT).show()
            numeroAleatorio()
        }
    }

    private fun actualizarMensajeMetodo() {
        when(nivel) {
            2 -> tituloNivel = "Sumas intermedias"
            3 -> tituloNivel = "Restas básicas"
            4 -> tituloNivel = "Sumas y restas"
            5 -> tituloNivel = "Multiplicación"
            6 -> tituloNivel = "División"
            7 -> tituloNivel = "Multiplicacion y division"
            8 -> tituloNivel = "Todas las operaciones"
            9 -> tituloNivel = "Infinito"
        }
        mensajeNivel = "Nivel $nivel - $tituloNivel"
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
        values.put("nombre", nombreJugador)
        values.put("score", score)

        db.insert("puntaje", null, values)
        db.close()
    }

    private fun comparar() {
        var manzanas = "manzanas"
        var quedan = "quedan"
        val respuesta = et_Respuesta.text.toString()
        if (respuesta.isNotEmpty()) {
            val respuestaJugador = respuesta.toInt()

            if (respuestaJugador == resultado) {
                mpGreat.start()
                score ++
                tv_score.text = "Score: $score"
                Toast.makeText(requireContext(), "Correcto!", Toast.LENGTH_SHORT).show()
            } else {
                mpBad.start()
                vidas--
                updateVidasUI()

                if (vidas == 0) { // Si se quedan sin vidas
                    Toast.makeText(requireContext(), "¡Perdiste todas tus vidas!", Toast.LENGTH_SHORT).show()

                    val fragment = MainFragment()
                    val bundle = Bundle()
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }
                else {
                    if(vidas == 1) {
                        manzanas = "manzana"
                        quedan = "queda"
                    }
                    Toast.makeText(requireContext(), "Te equivocaste! Te $quedan $vidas $manzanas!", Toast.LENGTH_SHORT).show()
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
            3 -> {
                iv_Vidas.setImageResource(R.drawable.tresvidas)
            }
            2 -> {
                iv_Vidas.setImageResource(R.drawable.dosvidas)
            }
            1 -> {
                iv_Vidas.setImageResource(R.drawable.unavida)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mp.stop()
        mp.release()
        mpGreat.release()
        mpBad.release()
    }

    private fun nivel1(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()

        resultado = numeroAleatorioUno + numeroAleatorioDos
        if (resultado > 10) {
            suma()
        }
        else {
            iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
            iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
        }
    }

    private fun suma(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno + numeroAleatorioDos
        iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
        iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
        iv_Signo.setImageResource(R.drawable.adicion)
    }

    private fun resta(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno - numeroAleatorioDos
        if (resultado < 0) {
           resta()
        }
        else {
            iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
            iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
            iv_Signo.setImageResource(R.drawable.resta)
        }
    }

    private fun nivel4(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if (numeroAleatorioUno > 0 && numeroAleatorioDos <= 4) {
            resultado = numeroAleatorioUno + numeroAleatorioDos;
            iv_Signo.setImageResource(R.drawable.adicion); // Establece la imagen de suma
        } else {
            resultado = (numeroAleatorioUno - numeroAleatorioDos);
            iv_Signo.setImageResource(R.drawable.resta);
            if(resultado<0)
                nivel4()
        }
        iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
        iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
    }

    private fun nivel5(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno * numeroAleatorioDos
        iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
        iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
        iv_Signo.setImageResource(R.drawable.multiplicacion)
    }

    private fun nivel6(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if(numeroAleatorioDos == 0)
            nivel6()
        else if(numeroAleatorioUno % numeroAleatorioDos != 0) {
            nivel6()
        }
        else{
            resultado = numeroAleatorioUno / numeroAleatorioDos
            iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
            iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
            iv_Signo.setImageResource(R.drawable.division)
        }
    }

    private fun nivel7(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if (numeroAleatorioUno > 0 && numeroAleatorioDos <= 4) {
            resultado = numeroAleatorioUno * numeroAleatorioDos;
            iv_Signo.setImageResource(R.drawable.multiplicacion); // Establece la imagen de suma
        } else if(numeroAleatorioDos == 0){
            nivel7()
        }
        else if(numeroAleatorioUno % numeroAleatorioDos != 0) {
            nivel7()
        }
        else{
            resultado = numeroAleatorioUno / numeroAleatorioDos
            iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
            iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
            iv_Signo.setImageResource(R.drawable.division)
        }
        iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
        iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
    }

    private fun nivel8(){
        when(((Math.random() * 4) + 1).toInt()){
            1->suma()
            2->resta()
            3->nivel5()
            4->nivel6()
        }
    }
    private fun restaVerificada(){
        resultado = numeroAleatorioUno - numeroAleatorioDos
        iv_Auno.setImageResource(getDrawableId(numeroAleatorioUno))
        iv_Ados.setImageResource(getDrawableId(numeroAleatorioDos))
        iv_Signo.setImageResource(R.drawable.resta)
    }
}
