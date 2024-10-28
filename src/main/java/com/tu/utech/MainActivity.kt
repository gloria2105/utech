package com.tu.utech

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tu.utech.ui.theme.UTECHTheme


class MainActivity : ComponentActivity() {
    private lateinit var mapView: MapView
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        setContent {
            UTECHTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHostSetup(savedInstanceState)
                }
            }
        }


        mapView = MapView(this)
        mapView.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun insertTestData(name: String, email: String, order: String) {
        dbHelper.insertUser(name, email, order)
    }
}

@Composable
fun NavHostSetup(savedInstanceState: Bundle?) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("order_confirmation/{name}/{order}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "Desconocido"
            val order = backStackEntry.arguments?.getString("order") ?: "No disponible"
            OrderConfirmationScreen(name, order)
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var order by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dbHelper = DatabaseHelper(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.laptop), contentDescription = "Logo", modifier = Modifier.size(100.dp))
        Text(text = "UTECH STATUS APP", style = MaterialTheme.typography.headlineSmall)

        TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        TextField(value = order, onValueChange = { order = it }, label = { Text("Order") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = {
            if (name.isNotEmpty() && email.isNotEmpty() && order.isNotEmpty()) {
                dbHelper.insertUser(name, email, order) // Inserta datos en la base de datos
                navController.navigate("order_confirmation/$name/$order")
            } else {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Login")
        }

        Button(onClick = {
            fetchDollarExchangeRate(context) { rate ->
                Toast.makeText(context, "Tipo de cambio: $rate", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.padding(top = 16.dp)) {  // Añadir padding para separarlo
            Text("Consultar Tipo de Cambio")
        }
    }
}

@Composable
fun OrderConfirmationScreen(name: String, order: String) {
    val deliveryDate = "LLEGARA EL MARTES 07 DE SEPTIEMBRE, 11:00 AM - 1:00 PM"
    val address = "CALLE LUZ 15, COLONIA JUAREZ, CP 12300, CDMX"

    var showMap by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pedido Confirmado", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Nombre: $name", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Pedido: $order")
        Text(text = "Fecha: $deliveryDate")
        Text(text = "Dirección: $address")
        // Iconos en la parte superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconWithLabel(R.drawable.icon_confirmado, "Confirmado")
            IconWithLabel(R.drawable.icon_en_camino, "En camino")
            IconWithLabel(R.drawable.icon_en_ruta_de_entrega, "En ruta de entrega")
            IconWithLabel(R.drawable.icon_entregado, "Entregado")
        }

        Button(onClick = { showMap = !showMap }) {
            Text(if (showMap) "Ocultar Mapa" else "Mostrar Mapa")
        }

        AnimatedVisibility(visible = showMap) {
            MapView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Button(onClick = { /* Manejar lógica de reprogramar */ }) {
            Text("REPROGRAMAR")
        }
    }
}

@Composable
fun IconWithLabel(iconResId: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CustomerSupportScreen() {
    val orderId = "#72435189"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Atención al cliente", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Pedido: $orderId")

        Button(onClick = { /* Handle Send Email Logic */ }) {
            Text("Enviar")
        }

        Button(onClick = { /* Handle Live Chat Logic */ }) {
            Text("Chat en vivo")
        }
    }
}

@Composable
fun MapView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    AndroidView({ mapView }) { mapView ->
        mapView.onCreate(null)
        mapView.getMapAsync(OnMapReadyCallback { googleMap ->
            val location = LatLng(19.4326, -99.1332)
            googleMap.addMarker(MarkerOptions().position(location).title("Ubicación"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        })
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UTECHTheme {
        LoginScreen(rememberNavController())
    }
}

@Composable
fun ExchangeRateButton() {
    val context = LocalContext.current
    var exchangeRate by remember { mutableStateOf("") }

    Button(onClick = {
        fetchDollarExchangeRate(context) { rate ->
            exchangeRate = rate
        }
    }) {
        Text("Consultar Tipo de Cambio")
    }

    if (exchangeRate.isNotEmpty()) {
        Text(text = "Tipo de cambio: $exchangeRate", style = MaterialTheme.typography.bodyLarge)
    }
}

