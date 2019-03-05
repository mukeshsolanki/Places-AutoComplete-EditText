package `in`.madapps.placeautocompleteedittext

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter
import `in`.madapps.placesautocomplete.listener.OnPlacesDetailsListener
import `in`.madapps.placesautocomplete.model.Place
import `in`.madapps.placesautocomplete.model.PlaceDetails
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main activity show cases an example of how to use the places auto complete api
 */
class MainActivity : AppCompatActivity() {

  val placesApi = PlaceAPI.Builder()
    .apiKey("YOUR_API_KEY")
    .build(this@MainActivity)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    autoCompleteEditText.setAdapter(PlacesAutoCompleteAdapter(this, placesApi))
    autoCompleteEditText.onItemClickListener =
      AdapterView.OnItemClickListener { parent, _, position, _ ->
        val place = parent.getItemAtPosition(position) as Place
        autoCompleteEditText.setText(place.description)
        getPlaceDetails(place.id)
      }
  }

  private fun getPlaceDetails(placeId: String) {
    placesApi.fetchPlaceDetails(placeId, object :
      OnPlacesDetailsListener {
      override fun onError(errorMessage: String) {
        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
      }

      override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
        setupUI(placeDetails)
      }
    })
  }

  private fun setupUI(placeDetails: PlaceDetails) {
    var street = ""
    var city = ""
    var state = ""
    var country = ""
    var zipCode = ""
    val address = placeDetails.address
    (0 until address.size).forEach { i ->
      when {
        address[i].type.contains("street_number") -> street += address[i].shortName + " "
        address[i].type.contains("route") -> street += address[i].shortName
        address[i].type.contains("locality") -> city += address[i].shortName
        address[i].type.contains("administrative_area_level_1") -> state += address[i].shortName
        address[i].type.contains("country") -> country += address[i].shortName
        address[i].type.contains("postal_code") -> zipCode += address[i].shortName
      }
    }
    runOnUiThread {
      streetTextView.text = street
      cityTextView.text = city
      stateTextView.text = state
      countryTextView.text = country
      zipCodeTextView.text = zipCode
    }
  }
}
