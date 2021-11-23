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
import com.wangzhen.openrc.utils.SummerTools.runOnIo
import com.wangzhen.openrc.adapter.*
import com.wangzhen.openrc.data.Data
import com.wangzhen.openrc.data.InputSetting
import com.wangzhen.openrc.dialog.InputDialog
import com.wangzhen.openrc.dialog.RemindDialog
import com.wangzhen.openrc.dialog.SelectListDialog
import com.wangzhen.openrc.vm.SettingViewModel
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.fragment_input.view.*
import kotlinx.android.synthetic.main.fragment_revicer_setting.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AutoResetSettingFragment : Fragment() {
    var rvList: ArrayList<RecyclerView> = ArrayList()
    val settingViewModel: SettingViewModel by sharedViewModel()
    lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_auto_reset, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvList()
        settingViewModel.modelListChange.observe(viewLifecycleOwner){
            initRvList()
        }
    }

    private fun initRvList() {
        initModelList()
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


    var userModelAdapter = UserModelAdapter()
    private fun initModelList() {
        rootView.model_rv.layoutManager = LinearLayoutManager(context).apply {
            this.orientation = LinearLayoutManager.VERTICAL
        }

        rootView.model_rv.itemDecorationCount.takeIf { it <= 0 }?.let {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)!!)
            rootView.model_rv.addItemDecoration(divider)
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

}