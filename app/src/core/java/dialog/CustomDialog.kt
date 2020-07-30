package dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window.FEATURE_NO_TITLE
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.xodus.templatethree.R
import main.ApplicationClass
import util.getScreenWidth


class CustomDialog(private val appClass: ApplicationClass, private val customView: Int = R.layout.dialog_custom) : DialogFragment() {

    //Element
    private var isInitComplete: Boolean = false
    private var title: String = ""
    private var content: String = ""
    private var positiveText: String = ""
    private var negativeText: String = ""
    private var neutralText: String = ""
    private var onPositive: (CustomDialog) -> Unit = {}
    private var onNegative: (CustomDialog) -> Unit = {}
    private var onNeutral: (CustomDialog) -> Unit = {}
    private var showProgress: Boolean = false
    private var showButtons: Boolean = false
    private var isTitleCenter: Boolean = false
    private var isContentCenter: Boolean = false
    private var titleColor: Int = 0
    private var contentColor: Int = 0
    private var positiveColor: Int = 0
    private var negativeColor: Int = 0
    private var neutralColor: Int = 0
    private var onViewCreated: (CustomDialog, View) -> (Unit) = { _, _ -> }
    private var onViewCreatedSet = false

    //View
    private lateinit var tvTitle: MaterialTextView
    private lateinit var tvContent: MaterialTextView
    private lateinit var pbProgress: ProgressBar
    private lateinit var llButton: LinearLayout
    private lateinit var btnNeutral: MaterialButton
    private lateinit var btnNegative: MaterialButton
    private lateinit var btnPositive: MaterialButton

    fun onViewCreated(onViewCreated: (CustomDialog, View) -> Unit): CustomDialog {
        this.onViewCreated = onViewCreated
        onViewCreatedSet = true
        return this
    }

    fun showLoading(show: Boolean): CustomDialog {
        showProgress = show
        bind()
        return this
    }

    fun setTitle(text: Int): CustomDialog {
        title = appClass.resources.getString(text)
        bind()
        return this
    }

    fun setTitle(text: String): CustomDialog {
        title = text
        bind()
        return this
    }

    fun setTitleColor(color: Int): CustomDialog {
        titleColor = color
        bind()
        return this
    }

    fun setTitleCenter(): CustomDialog {
        isTitleCenter = true
        bind()
        return this
    }

    fun setContent(text: Int): CustomDialog {
        content = appClass.resources.getString(text)
        bind()
        return this
    }

    fun setContent(text: String): CustomDialog {
        content = text
        bind()
        return this
    }

    fun setContentColor(color: Int): CustomDialog {
        contentColor = color
        bind()
        return this
    }


    fun setContentCenter(): CustomDialog {
        isContentCenter = true
        bind()
        return this
    }

    fun setPositiveText(text: Int): CustomDialog {
        showButtons = true
        positiveText = appClass.resources.getString(text)
        bind()
        return this
    }

    fun setPositiveText(text: String): CustomDialog {
        showButtons = true
        positiveText = text
        bind()
        return this
    }

    fun setPositiveColor(color: Int): CustomDialog {
        positiveColor = color
        bind()
        return this
    }

    fun onPositive(action: (CustomDialog) -> Unit): CustomDialog {
        showButtons = true
        onPositive = action
        bind()
        return this
    }

    fun setNegativeText(text: String): CustomDialog {
        showButtons = true
        negativeText = text
        bind()
        return this
    }

    fun setNegativeText(text: Int): CustomDialog {
        showButtons = true
        negativeText = appClass.resources.getString(text)
        bind()
        return this
    }

    fun setNegativeColor(color: Int): CustomDialog {
        negativeColor = color
        bind()
        return this
    }

    fun onNegative(action: (CustomDialog) -> Unit): CustomDialog {
        showButtons = true
        onNegative = action
        bind()
        return this
    }

    fun setNeutralText(text: String): CustomDialog {
        showButtons = true
        neutralText = text
        bind()
        return this
    }

    fun setNeutralText(text: Int): CustomDialog {
        showButtons = true
        neutralText = appClass.resources.getString(text)
        bind()
        return this
    }

    fun setNeutralColor(color: Int): CustomDialog {
        neutralColor = color
        bind()
        return this
    }

    fun onNeutral(action: (CustomDialog) -> Unit): CustomDialog {
        showButtons = true
        onNeutral = action
        bind()
        return this
    }

    fun showButtons(show: Boolean): CustomDialog {
        this.showButtons = show
        bind()
        return this
    }

    fun setCancelabel(cancelable: Boolean): CustomDialog {
        isCancelable = cancelable
        return this
    }

    fun show(fragmentManager: FragmentManager?, onViewCreated: (CustomDialog, View) -> (Unit) = { _, _ -> }): CustomDialog {
        if (onViewCreatedSet.not())
            this.onViewCreated = onViewCreated
        fragmentManager?.let {
            show(it, this.javaClass.simpleName)
        }
        return this
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(customView, container, false)
        if (customView == R.layout.dialog_custom) {
            init(view)
            bind()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(this, view)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(requireActivity())
        root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // creating the fullscreen dialog
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout((getScreenWidth() / 100) * 90, ViewGroup.LayoutParams.WRAP_CONTENT)
        return dialog
    }

    private fun init(v: View) {
        tvTitle = v.findViewById(R.id.dialogCustom_tvTitle)
        tvContent = v.findViewById(R.id.dialogCustom_tvContent)
        tvContent.movementMethod = ScrollingMovementMethod()
        pbProgress = v.findViewById(R.id.dialogCustom_pbProgress)
        llButton = v.findViewById(R.id.dialogCustom_llButton)
        btnPositive = v.findViewById(R.id.dialogCustom_btnPositive)
        btnNegative = v.findViewById(R.id.dialogCustom_btnNegative)
        btnNeutral = v.findViewById(R.id.dialogCustom_btnNeutral)
        isInitComplete = true
    }

    private fun bind() {
        if (isInitComplete) {
            //Title
            tvTitle.text = title
            if (titleColor == 0) {
                tvTitle.setTextColor(ContextCompat.getColor(appClass, R.color.md_black_1000))
            } else {
                tvTitle.setTextColor(titleColor)
            }
            if (isTitleCenter) {
                tvTitle.gravity = Gravity.CENTER
            }
            //Content
            tvContent.text = content
            if (contentColor == 0) {
                tvContent.setTextColor(ContextCompat.getColor(appClass, R.color.md_black_1000))
            } else {
                tvContent.setTextColor(contentColor)
            }
            if (isContentCenter) {
                tvContent.gravity = Gravity.CENTER
            }
            //Progress
            if (showProgress) {
                pbProgress.visibility = View.VISIBLE
            } else {
                pbProgress.visibility = View.GONE
            }
            //Buttons
            if (showButtons) {
                llButton.visibility = View.VISIBLE
            } else {
                llButton.visibility = View.GONE
            }
            //Right
            if(positiveText.isEmpty()) {
                btnPositive.visibility = View.INVISIBLE
            }else{
                btnPositive.text = positiveText
            }
            if (positiveColor == 0) {
                btnPositive.setBackgroundColor(ContextCompat.getColor(appClass, R.color.md_green_700))
            } else {
                btnPositive.setBackgroundColor(positiveColor)
            }
            btnPositive.setOnClickListener {
                dismiss()
                onPositive(this)
            }
            //Negative
            if(negativeText.isEmpty()){
                btnNegative.visibility = View.INVISIBLE
            }else {
                btnNegative.text = negativeText
            }
            if (negativeColor == 0) {
                btnNegative.setBackgroundColor(ContextCompat.getColor(appClass, R.color.md_red_700))
            } else {
                btnNegative.setBackgroundColor(negativeColor)
            }
            btnNegative.setOnClickListener {
                dismiss()
                onNegative(this)
            }
            //Neutral
            if(neutralText.isEmpty()){
                btnNeutral.visibility = View.INVISIBLE
            }else {
                btnNeutral.text = neutralText
            }
            if (neutralColor == 0) {
                btnNeutral.setBackgroundColor(ContextCompat.getColor(appClass, R.color.md_amber_500))
            } else {
                btnNeutral.setBackgroundColor(neutralColor)
            }
            btnNeutral.setOnClickListener {
                dismiss()
                onNeutral(this)
            }
        }
    }

}