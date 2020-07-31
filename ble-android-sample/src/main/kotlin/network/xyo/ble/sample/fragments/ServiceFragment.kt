package network.xyo.ble.sample.fragments

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_SECONDARY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_service.view.*
import network.xyo.ble.sample.R
import network.xyo.ble.sample.adapters.XYCharacteristicAdapter

@ExperimentalUnsignedTypes
class ServiceFragment : Fragment() {
    private var service: BluetoothGattService? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.service_uuid_title.text = service?.uuid.toString()
        view.service_type.text = getServiceType()

        val recyclerView = view.characteristic_list
        val characteristicList = XYCharacteristicAdapter(service?.characteristics?.toTypedArray()
                ?: arrayOf())
        val manager = LinearLayoutManager(activity?.applicationContext, RecyclerView.VERTICAL, false)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = characteristicList

        characteristicList.addListener(this.toString(), object : XYCharacteristicAdapter.Companion.XYCharacteristicAdapterListener {
            override fun onClick(service: BluetoothGattCharacteristic) {
                val transition = fragmentManager?.beginTransaction()
                val serviceFragment = CharacteristicFragment.newInstance(service)
                transition?.replace(R.id.root_frame_services, serviceFragment)
                transition?.addToBackStack(null)
                transition?.commit()
            }
        })
    }

    private fun getServiceType(): String {
        when (service?.type) {
            SERVICE_TYPE_PRIMARY -> return "SERVICE_TYPE_PRIMARY"
            SERVICE_TYPE_SECONDARY -> return "SERVICE_TYPE_SECONDARY"
        }
        return "UNKNOWN"
    }

    companion object {
        fun newInstance(service: BluetoothGattService): ServiceFragment {
            val frag = ServiceFragment()
            frag.service = service
            return frag
        }
    }
}
