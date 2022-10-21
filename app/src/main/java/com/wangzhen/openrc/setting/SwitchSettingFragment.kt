package com.wangzhen.openrc.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wangzhen.openrc.R
import com.wangzhen.openrc.adapter.CommonAdapter
import com.wangzhen.openrc.adapter.UserModelAdapter
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.dialog.SelectListDialog
import com.wangzhen.openrc.utils.SummerTools
import kotlinx.android.synthetic.main.fragment_switch_setting.view.*


class SwitchSettingFragment : Fragment() {
    lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_switch_setting, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvList()
    }

    private var userModelAdapter = UserModelAdapter()
    private fun initModelList() {
        rootView.model_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }


        rootView.model_rv.adapter = userModelAdapter
        userModelAdapter.setOnClick { pos ->
            SummerTools.runOnIo {
                Data.db.inputSettingDao().getAll()?.let {
                    Data.loadSetting(it[pos])
                    activity?.runOnUiThread {
                        initRvList()
                        userModelAdapter.setPos(pos)
                    }
                }
            }
        }
        loadSettingDbData()
    }

    private var switchAdapter = CommonAdapter()
    private fun initSwitchList() {
        rootView.switch_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.switch_rv.adapter = switchAdapter
        switchAdapter.setOnClick { pos ->

        }
        switchAdapter.setDataList(Data.switchStringList)
    }

    private var switchWithGpioAdapter = CommonAdapter()
    private fun initSwitchWithGpioList() {
        rootView.gpio_input_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.gpio_input_rv.adapter = switchWithGpioAdapter
        switchWithGpioAdapter.setOnClick { pos ->

        }
        switchWithGpioAdapter.setDataList(Data.switchWithGpioList.map {
            if (it == -1) {
                "无"
            } else {
                Data.gpioList[it].name
            }
        })
    }

    private var switchShowAdapter = CommonAdapter()
    private fun initSwitchShowList() {
        rootView.show_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.show_rv.adapter = switchShowAdapter
        switchShowAdapter.setOnClick { pos ->

        }
        switchShowAdapter.setDataList(Data.switchShowList.map {
            if (it == 1) {
                "开"
            } else {
                "关"
            }
        })
    }

    private var switchActiveAdapter = CommonAdapter()
    private fun initSwitchActiveList() {
        rootView.active_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.active_rv.adapter = switchActiveAdapter
        switchActiveAdapter.setOnClick { pos ->
            SelectListDialog(
                Data.switchOffOnList,
                Data.switchActiveList[pos]
            ) { selectPos ->
                Data.switchActiveList[pos] = selectPos
                bindDataSwitchActiveAdapter()
            }.show(requireActivity().supportFragmentManager, "")
        }
        bindDataSwitchActiveAdapter()
    }

    private fun bindDataSwitchActiveAdapter() {
        switchActiveAdapter.setDataList(Data.switchActiveList.map {
            if (it == 1) {
                "开"
            } else {
                "关"
            }
        })
        switchActiveAdapter.notifyDataSetChanged()
    }

    private var switch1Adapter = CommonAdapter()
    private fun initSwitch1List() {
        rootView.value1_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.value1_rv.adapter = switch1Adapter
        switch1Adapter.setOnClick { pos ->

        }
        switch1Adapter.setDataList(Data.switch1ValueList.map {
            "$it"
        })
    }

    private var switch2Adapter = CommonAdapter()
    private fun initSwitch2List() {
        rootView.value2_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.value2_rv.adapter = switch2Adapter
        switch2Adapter.setOnClick { pos ->

        }
        switch2Adapter.setDataList(Data.switch2ValueList.map {
            "$it"
        })
    }

    private var switch3Adapter = CommonAdapter()
    private fun initSwitch3List() {
        rootView.value3_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.value3_rv.adapter = switch3Adapter
        switch3Adapter.setOnClick { pos ->

        }
        switch3Adapter.setDataList(Data.switch3ValueList.map {
            "$it"
        })
    }

    private fun initRvList() {
        initModelList()
        initSwitchList()
        initSwitchWithGpioList()
        initSwitchShowList()
        initSwitchActiveList()
        initSwitch1List()
        initSwitch2List()
        initSwitch3List()
    }

    private fun loadSettingDbData() {
        SummerTools.runOnIo {
            Data.db.inputSettingDao().getAll()?.let { list ->
                Log.e("getAll", "----" + Gson().toJson(list))
                activity?.runOnUiThread {
                    userModelAdapter.setDataList(list)
                    userModelAdapter.notifyDataSetChanged()
                }

                Data.mInputSetting?.let {
                    list.forEachIndexed { index, inputSetting ->
                        if (inputSetting.name == it.name) {
                            activity?.runOnUiThread {
                                userModelAdapter.setPos(index)
                            }
                        }
                    }
                }
                return@runOnIo
            }
        }
    }
}