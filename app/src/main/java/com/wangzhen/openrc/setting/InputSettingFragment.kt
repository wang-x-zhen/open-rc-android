package com.wangzhen.openrc.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.wangzhen.openrc.R
import com.wangzhen.openrc.ScaleDialog
import com.wangzhen.openrc.adapter.*
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.data.InputSetting
import com.wangzhen.openrc.dialog.InputDialog
import com.wangzhen.openrc.dialog.RemindDialog
import com.wangzhen.openrc.dialog.SelectListDialog
import com.wangzhen.openrc.utils.SummerTools.runOnIo
import com.wangzhen.openrc.vm.SettingViewModel
import kotlinx.android.synthetic.main.fragment_input.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class InputSettingFragment : Fragment() {
    var rvList: ArrayList<RecyclerView> = ArrayList()
    private val settingViewModel: SettingViewModel by sharedViewModel()
    lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_input, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvList()
        rootView.model_rv.itemDecorationCount.takeIf { it <= 0 }?.let {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.custom_divider
                )!!
            )
            rootView.model_rv.addItemDecoration(divider)
        }
        rootView.auto_reset_rv.itemDecorationCount.takeIf { it <= 0 }?.let {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.custom_divider
                )!!
            )
            rootView.model_rv.addItemDecoration(divider)
        }
        rootView.saveSetting.setOnClickListener {
            var _name = ""
            Data.mInputSetting?.let {
                _name = it.name
            }

            fragmentManager?.let { it1 ->
                val inputSetting = InputSetting().apply {
                    name = _name
                    time = System.currentTimeMillis()
                    gpio2InputList = Data.gpio2InputList
                    gpio2PwmList = Data.gpio2PwmList
                    gpio2DirectionList = Data.gpio2DirectionList
                    autoResetList = Data.autoResetList
                    inputScaleList = Data.inputScaleList
                    switchWithGpioList = Data.switchShowList
                    switchShowList = Data.switchShowList
                    switchActiveList = Data.switchActiveList
                    switch1ValueList = Data.switch1ValueList
                    switch2ValueList = Data.switch2ValueList
                    switch3ValueList = Data.switch3ValueList
                }
                InputDialog(_name) {
                    inputSetting.name = it
                    runOnIo {
                        Data.db.inputSettingDao().insertAll(inputSetting)
                        resetPos()
                        loadSettingDbData()
                        activity?.runOnUiThread {
                            settingViewModel.modelListChange.value = System.currentTimeMillis()
                        }
                    }
                }.show(it1, "xxx")
            }
        }
        rootView.deleteSetting.setOnClickListener {
            fragmentManager?.let { it1 ->
                RemindDialog("删除这个模型配置？") {
                    runOnIo {
                        Data.mInputSetting?.let {
                            Data.mInputSetting = null
                            Data.db.inputSettingDao().deleteByName(it.name)
                            resetPos()
                            loadSettingDbData()
                            settingViewModel.modelListChange.value = System.currentTimeMillis()
                        }
                    }
                }.show(it1, "xxx")
            }
        }
    }

    private fun initRvList() {
        initModelList()
        initGpiolList()
        initInput()
        initPwmMap()
        initDirectionMap()
        initAutoResetList()
        initScaleList()
        linkRvList()
    }

    private fun resetPos() {
        Data.db.inputSettingDao().getAll()?.takeIf { !it.isNullOrEmpty() }?.let {
            Data.loadSetting(it[0])
            activity?.runOnUiThread {
                initRvList()
                userModelAdapter.setPos(0)
            }
            return
        }

    }

    private fun loadSettingDbData() {
        runOnIo {
            Data.db.inputSettingDao().getAll()?.let { list ->
                Log.e("getAll", "----" + Gson().toJson(list))
                activity?.runOnUiThread {
                    userModelAdapter.setDataList(list)
                    userModelAdapter.notifyDataSetChanged()
                }
                activity?.runOnUiThread {
                    if (!list.isNullOrEmpty()) {
                        rootView.deleteSetting.visibility = View.VISIBLE
                    } else {
                        rootView.deleteSetting.visibility = View.GONE
                    }
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
        activity?.runOnUiThread {
            rootView.deleteSetting.visibility = View.GONE
        }
    }


    var userModelAdapter = UserModelAdapter()
    private fun initModelList() {
        rootView.model_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }


        rootView.model_rv.adapter = userModelAdapter
        userModelAdapter.setOnClick { pos ->
            runOnIo {
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

    private fun initGpiolList() {
        var adapter = GpioAdapter()
        rootView.gpio_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        rootView.gpio_rv.adapter = adapter
        adapter.setDataList(Data.gpioList)
        adapter.setOnClick {
            return@setOnClick null
        }
    }


    private fun initInput() {
        var adapter = GpioInputAdapter()
        rootView.gpio_input_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        rootView.gpio_input_rv.adapter = adapter
        adapter.setOnClick {
            val pinPos = Data.gpio2InputList[it]!!
            SelectListDialog(Data.inputList.map { it.name }, pinPos) { selectPos ->
                Data.gpio2InputList[it] = selectPos
                adapter.notifyDataSetChanged()
            }
                .show(requireActivity().supportFragmentManager, "")
        }
    }

    private fun initPwmMap() {
        var adapter = GpioPwmAdapter()
        rootView.gpio_pwm_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        rootView.gpio_pwm_rv.adapter = adapter
        adapter.setOnClick {
            val pwmPos = Data.gpio2PwmList[it]
            SelectListDialog(Data.pwmList.map { it.name }, pwmPos) { selectPos ->
                Data.gpio2PwmList[it] = selectPos
                adapter.notifyDataSetChanged()
            }.show(requireActivity().supportFragmentManager, "")
        }
    }

    private fun initDirectionMap() {
        var adapter = GpioDirectionAdapter()
        rootView.gpio_direction_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        rootView.gpio_direction_rv.adapter = adapter
        adapter.setOnClick { pos ->
            val directionPos = Data.gpio2DirectionList[pos]

            SelectListDialog(Data.directionList.map { it.name }, directionPos) { selectPos ->
                Data.gpio2DirectionList[pos] = selectPos
                adapter.notifyDataSetChanged()
            }
                .show(requireActivity().supportFragmentManager, "")
        }
    }

    private fun initAutoResetList() {
        rootView.auto_reset_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.auto_reset_rv.adapter = InputAdapter()
    }

    private fun initScaleList() {
        rootView.scale_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        rootView.scale_rv.adapter = GpioScaleAdapter().apply {
            setOnClick { pos ->
                ScaleDialog(value = Data.inputScaleList[pos] / 100F) { value ->
                    Data.inputScaleList[pos] = (value * 100).toInt()
                    this.notifyDataSetChanged()
                }.show(requireActivity().supportFragmentManager, "")
            }
        }
    }


    private fun linkRvList() {
        rvList.apply {
            add(rootView.gpio_rv)
            add(rootView.gpio_input_rv)
            add(rootView.gpio_pwm_rv)
            add(rootView.gpio_direction_rv)
        }
        rvList.forEachIndexed { index, recyclerView ->
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (RecyclerView.SCROLL_STATE_IDLE != recyclerView.scrollState) {
                        rvList.forEachIndexed { indexIn, recyclerViewIn ->
                            if (recyclerViewIn != recyclerView) {
                                recyclerViewIn.scrollBy(dx, dy)
                            }
                        }
                    }
                }
            })
        }

    }
}