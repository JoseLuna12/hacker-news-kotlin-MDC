package com.joseluna.hr

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class Item_list_fragment : Fragment() {

    private lateinit var queue: RequestQueue
    private lateinit var refreshView: SwipeRefreshLayout
    private var adapter: ItemListReciclerViewAdapter = ItemListReciclerViewAdapter()

    private lateinit var currentView: View


    override fun onResume() {
        super.onResume()
        val supportActionBar = (activity as AppCompatActivity).supportActionBar
        supportActionBar?.subtitle = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        currentView = inflater.inflate(R.layout.fragment_item_list_fragment, container, false)
        val found  = currentView.findViewById<RecyclerView>(R.id.recycler_list_view)
        refreshView = currentView.findViewById(R.id.refreshLayout)
        found.layoutManager = LinearLayoutManager(activity)



        refreshView.setOnRefreshListener {
            getHrData(found,true)
        }

        refreshView.post {
            refreshView.isRefreshing = true
        }

        getHrData(found, false)
        return currentView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = Volley.newRequestQueue(requireContext())
    }

  private fun vibrate(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val vib = context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibration = vib.defaultVibrator
            vibration.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }else{
            val vib= context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }


    private fun getHrData(listView: RecyclerView, reload: Boolean){
        val url = "https://hn.algolia.com/api/v1/search?tags=front_page"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {
                val dataToShow = arrayListOf<hacker_rank_news>()
                val hrJson = JSONObject(it)
                val hits = hrJson.getJSONArray("hits")

                for (i in 0 until hits.length()){
                    val item = hits.getJSONObject(i)
                    val title = item.getString("title")
                    val id = item.getString("objectID")
                    val points = item.getInt("points")
                    val urlHR = item.getString("url")

                    val newData = hacker_rank_news(title, urlHR, id, points)
                    dataToShow.add(newData)
                    println(newData.title)
                }
                if(!reload){
                    adapter.setOnClickListener(object: RecyclerViewInterface {
                        override fun onItemClick(position: Int) {
                            vibrate()
                            val action = Item_list_fragmentDirections
                                .actionItemListFragment2ToHackerNewsContentDetail(
                                    dataToShow[position].url, dataToShow[position].title
                                )
                            findNavController().navigate(action)
                        }
                    })
                    adapter.initData(dataToShow)
                    listView.adapter = adapter
                }
                else{
                    val message = when(adapter.refresh(dataToShow)) {
                        ItemListReciclerViewAdapter.refreshStatus.NONE -> "Up to date"
                        ItemListReciclerViewAdapter.refreshStatus.UPDATED -> "News Updated"
                    }

                    Snackbar.make(currentView, message, Snackbar.LENGTH_SHORT).show()
                    vibrate()
                }

                refreshView.isRefreshing = false
            },
            {
                println("error")
            }
        )

        queue.add(stringRequest)
//                    Snackbar.make(currentView, "Updated News", Snackbar.LENGTH_SHORT).show()
    }
}