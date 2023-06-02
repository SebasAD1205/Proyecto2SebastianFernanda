package com.example.proyecto2sebastianfernanda


import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class NivelBaseFragment : Fragment() {
    private lateinit var tvNombre: TextView
    private lateinit var tvScore: TextView
    private lateinit var ivAuno: ImageView
    private lateinit var ivAdos: ImageView
    private lateinit var ivVidas: ImageView
    private lateinit var ivSigno: ImageView
    private lateinit var etRespuesta: EditText
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
    private var nivel = 1
    private var scoreLimite = 9
    private var tituloNivel="Sumas básicas"
    private var mensajeNivel = "Nivel $nivel - $tituloNivel"
    private lateinit var stringScoreAux: String
    private var toastMessage:Toast? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_base_nivel, container, false)
        cancelarToast()
        toastMessage = Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT)
        toastMessage?.show()

        tvNombre = view.findViewById(R.id.tv_nombre)
        tvScore = view.findViewById(R.id.tv_score)
        ivVidas = view.findViewById(R.id.iv_vidas)
        ivAuno = view.findViewById(R.id.iv_Auno)
        ivAdos = view.findViewById(R.id.iv_Ados)
        ivSigno = view.findViewById(R.id.iv_Signo)
        etRespuesta = view.findViewById(R.id.et_respuesta)
        btnComprobar = view.findViewById(R.id.btnComprobar)

        nombreJugador = arguments?.getString("jugador")!!
        val stringJugador ="Jugador: $nombreJugador"
        tvNombre.text =  stringJugador

        val stringScore = arguments?.getString("score")
        score = stringScore?.toInt() ?: 0
        stringScoreAux = "Score: $score"
        tvScore.text = stringScore

        val stringVidas = arguments?.getString("vidas")
        vidas = stringVidas?.toInt() ?: 3
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
                2-> nivel2()
                3-> nivel3()
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
            cancelarToast()
            toastMessage = Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT)
            toastMessage?.show()
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
        val bd = FirebaseFirestore.getInstance()
        val data = hashMapOf<String, Any>(
            "nombre" to nombreJugador,
            "score" to score
        )
        bd.collection("Jugadores")
            .document("3pIGRRf1se02EB8y6AvJ")
            .update(data)
    }

    private fun comparar() {
        var manzanas = "manzanas"
        var quedan = "quedan"
        val respuesta = etRespuesta.text.toString()
        if (respuesta.isNotEmpty()) {
            val respuestaJugador = respuesta.toInt()

            if (respuestaJugador == resultado) {
                mpGreat.start()
                score ++
                stringScoreAux = "Score: $score"
                tvScore.text = stringScoreAux

                cancelarToast()
                toastMessage =Toast.makeText(requireContext(), "Correcto!", Toast.LENGTH_SHORT)
                toastMessage?.show()
            } else {
                mpBad.start()
                vidas--
                updateVidasUI()

                if (vidas == 0) {
                    cancelarToast()
                    toastMessage = Toast.makeText(requireContext(), "¡Perdiste todas tus vidas!", Toast.LENGTH_SHORT)
                    toastMessage?.show()

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

                    cancelarToast()
                    toastMessage = Toast.makeText(requireContext(), "Te equivocaste! Te $quedan $vidas $manzanas!", Toast.LENGTH_SHORT)
                    toastMessage?.show()
                }
                baseDeDatos()
            }

            etRespuesta.setText("")
            numeroAleatorio()
        } else {
            cancelarToast()
            toastMessage = Toast.makeText(requireContext(), "Debes dar una respuesta", Toast.LENGTH_SHORT)
            toastMessage?.show()
        }
    }

    private fun updateVidasUI() {
        when (vidas) {
            3 -> {
                ivVidas.setImageResource(R.drawable.tresvidas)
            }
            2 -> {
                ivVidas.setImageResource(R.drawable.dosvidas)
            }
            1 -> {
                ivVidas.setImageResource(R.drawable.unavida)
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
            nivel1()
        }
        else {
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
        }
    }

    private fun nivel2(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno + numeroAleatorioDos
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
        ivSigno.setImageResource(R.drawable.adicion)
    }

    private fun nivel3(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno - numeroAleatorioDos
        if (resultado < 0) {
           nivel3()
        }
        else {
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            ivSigno.setImageResource(R.drawable.resta)
        }
    }

    private fun nivel4(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if (numeroAleatorioUno > 0 && numeroAleatorioDos <= 4) {
            resultado = numeroAleatorioUno + numeroAleatorioDos
            ivSigno.setImageResource(R.drawable.adicion)
        } else {
            resultado = (numeroAleatorioUno - numeroAleatorioDos)
            ivSigno.setImageResource(R.drawable.resta)
            if(resultado<0)
                nivel4()
        }
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
    }

    private fun nivel5(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno * numeroAleatorioDos
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
        ivSigno.setImageResource(R.drawable.multiplicacion)
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
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            ivSigno.setImageResource(R.drawable.division)
        }
    }

    private fun nivel7(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if (numeroAleatorioUno > 0 && numeroAleatorioDos <= 4) {
            resultado = numeroAleatorioUno * numeroAleatorioDos
            ivSigno.setImageResource(R.drawable.multiplicacion)
        } else if(numeroAleatorioDos == 0){
            nivel7()
        }
        else if(numeroAleatorioUno.toDouble() % numeroAleatorioDos.toDouble() != 0.0) {
            nivel7()
        }
        else{
            resultado = numeroAleatorioUno / numeroAleatorioDos
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            ivSigno.setImageResource(R.drawable.division)
        }
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
    }

    private fun nivel8(){
        when(((Math.random() * 4) + 1).toInt()){
            1->nivel2()
            2->nivel3()
            3->nivel5()
            4->nivel6()
        }
    }

    private fun cancelarToast(){
        if (toastMessage!= null) {
            toastMessage?.cancel()
        }
    }
}
