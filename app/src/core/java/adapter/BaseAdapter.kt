package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatethree.BR
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.*
import main.ApplicationClass
import main.CON_ANIMATIONS
import model.Template
import model.TemplateRoom
import util.animatePlaceholders
import util.animateViews
import viewmodel.TemplateRoomViewModel
import viewmodel.TemplateViewModel

open class BaseAdapter<T>(
    val showAdd: Boolean,
    var showLoading: Boolean = false,
    val sectionIndex: Int,
    val templateViewModel: TemplateViewModel? = null,
    val templateRoomViewModel: TemplateRoomViewModel? = null,
    //TODO : Add ViewModel Here
    private val addIsLeft: Boolean = true,
    private val isHorizontal: Boolean = false,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var list: ArrayList<T> = ArrayList()
    var bindList: ArrayList<Pair<Int, ViewDataBinding>> = arrayListOf()
    private var bindHashList: ArrayList<Int> = ArrayList()

    @LayoutRes
    private var placeholder: Int? = null
    private val CON_PlACEHOLDER_COUNT = 3
    private var isPlaceholder = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            -1 -> if (isHorizontal) {
                BaseAddHorizontalHolder(
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_add_horizontal, parent, false),
                    sectionIndex,
                    templateViewModel,
                    templateRoomViewModel,
                )
            } else {
                BaseAddVerticalHolder(
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_add_vertical, parent, false),
                    sectionIndex,
                    templateViewModel,
                    templateRoomViewModel,
                )
            }
            -2 -> if (isHorizontal) {
                BaseLoadingHorizontalHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_loading_horizontal, parent, false))
            } else {
                BaseLoadingVerticalHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_loading_vertical, parent, false))
            }
            -3 -> placeholder?.let {
                BasePlaceholderHolder(LayoutInflater.from(parent.context).inflate(it, parent, false))
            } ?: kotlin.run {
                BaseEmptyHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.section_empty, parent, false))
            }
            else -> BaseEmptyHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.section_empty, parent, false))
        }
    }

    override fun getItemCount(): Int = if (isPlaceholder) CON_PlACEHOLDER_COUNT else (list.size + (if (showAdd) 1 else 0) + (if (showLoading) 1 else 0))

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BasePlaceholderHolder -> holder.bindData()
            is BaseLoadingHorizontalHolder -> Unit
            is BaseAddVerticalHolder -> holder.bindData()
            is BaseAddHorizontalHolder -> holder.bindData()
            is BaseViewHolder<*> -> (holder as BaseViewHolder<T>).apply {
                bindData(list[if (showAdd) position - 1 else position])
                if (bindHashList.contains(adapterPosition).not()) {
                    bindHashList.add(adapterPosition)
                    if (CON_ANIMATIONS) {
                        animate()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isPlaceholder) {
            return -3
        }
        if (showAdd) {
            if (addIsLeft && position == 0) {
                return -1
            } else if (addIsLeft.not() && position == list.size) {
                return -1
            }
        }
        if (showLoading && position == list.size + if (showAdd) 1 else 0) {
            return -2
        }
        return position
    }


    companion object {
        open class BasePlaceholderHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bindData() {
                (view as ViewGroup).animatePlaceholders()
            }
        }

        open class BaseLoadingHorizontalHolder(private val binding: RowLoadingHorizontalBinding) : RecyclerView.ViewHolder(binding.root)
        open class BaseLoadingVerticalHolder(private val binding: RowLoadingVerticalBinding) : RecyclerView.ViewHolder(binding.root)
        open class BaseEmptyHolder(binding: SectionEmptyBinding) : RecyclerView.ViewHolder(binding.root)
        open class BaseAddHorizontalHolder(
            private val binding: RowAddHorizontalBinding,
            private val sectionIndex: Int,
            val templateViewModel: TemplateViewModel? = null,
            val templateRoomViewModel: TemplateRoomViewModel? = null,
            //TODO : Add ViewModel Here
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bindData() {
                binding.setVariable(BR.viewModel, templateViewModel ?: templateRoomViewModel) //TODO : Add ViewModel Here
                binding.setVariable(BR.position, sectionIndex)
                binding.setVariable(BR.appClass, ApplicationClass.getInstance())
            }
        }

        open class BaseAddVerticalHolder(
            private val binding: RowAddVerticalBinding,
            private val sectionIndex: Int,
            val templateViewModel: TemplateViewModel? = null,
            val templateRoomViewModel: TemplateRoomViewModel? = null,
            //TODO : Add ViewModel Here
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bindData() {
                binding.setVariable(BR.viewModel, templateViewModel ?: templateRoomViewModel) //TODO : Add ViewModel Here
                binding.setVariable(BR.position, sectionIndex)
                binding.setVariable(BR.appClass, ApplicationClass.getInstance())
            }
        }

        open class BaseViewHolder<T>(
            private val binding: ViewDataBinding,
            val templateViewModel: TemplateViewModel? = null,
            val templateRoomViewModel: TemplateRoomViewModel? = null,
            //TODO : Add ViewModel Here
        ) : RecyclerView.ViewHolder(binding.root) {
            val context: Context = binding.root.context
            open fun bindData(data: T) {
                binding.apply {
                    setVariable(BR.viewModel, templateViewModel ?: templateRoomViewModel) //TODO : Add ViewModel Here
                    setVariable(BR.position, adapterPosition)
                    setVariable(BR.data, data)
                    setVariable(BR.appClass, ApplicationClass.getInstance())
                    executePendingBindings()
                }
            }

            fun animate() {
                binding.apply {
                    root.post {
                        when (this) {
                            is RowTemplateBinding -> animateViews(arrayOf(rowTemplateTvNumber), false, 0, 0, 0) {
                                animateViews(arrayOf(rowTemplateTvNumber), true)
                            }
                            is RowTemplateRoomBinding -> animateViews(arrayOf(rowTemplateRoomTvNumber), false, 0, 0, 0) {
                                animateViews(arrayOf(rowTemplateRoomTvNumber), true)
                            }
                            //TODO : Add Row Binding Animation Here
                        }
                    }
                }
            }
        }

    }

    @Suppress("UNCHECKED_CAST")
    fun updateList(newList: ArrayList<T>) {
        //Use this, or clone list before updating to have update animations.
        if (newList.isNotEmpty()) {
            val tmp: ArrayList<T>? = when (newList[0]) {
                is Template -> Template.cloneList(newList as ArrayList<Template>) as ArrayList<T>
                is TemplateRoom -> TemplateRoom.cloneList(newList as ArrayList<TemplateRoom>) as ArrayList<T>
                //TODO : Add Model Cloning Here
                else            -> null
            }
            if (tmp != null) {
                newList.clear()
                newList.addAll(tmp)
            }
        }
        isPlaceholder = false
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return list.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] === newList[newItemPosition]
            }
        })
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setPlaceholder(@LayoutRes placeholder: Int) {
        if (list.isEmpty() && isPlaceholder.not()) {
            isPlaceholder = true
            this.placeholder = placeholder
            notifyDataSetChanged()
        }
    }
}


