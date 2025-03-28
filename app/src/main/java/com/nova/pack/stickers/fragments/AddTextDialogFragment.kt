package com.nova.pack.stickers.fragments


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nova.pack.stickers.R
import com.nova.pack.stickers.adapter.ColorPickerAdapter
import com.nova.pack.stickers.adapter.FontPickerAdapter
import com.nova.pack.stickers.databinding.AddTextDialogBinding
import com.nova.pack.stickers.listener.OnColorPickerListener
import com.nova.pack.stickers.listener.OnFontPickerClickListener
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class AddTextDialogFragment : DialogFragment(),OnColorPickerListener,OnFontPickerClickListener  {
    private lateinit var mInputMethodManager: InputMethodManager
    private var mColorCode = 0
    private lateinit var typeface:Typeface
    private lateinit var binding : AddTextDialogBinding
    private var mTextEditorListener: TextEditorListener? = null
    private val fontList = listOf("alice_regular.ttf", "bebas_regular.ttf","rubrik_bubble.ttf","roboto_regular.ttf")
    private var isKeyboardVisible = false


    interface TextEditorListener {
        fun onDone(inputText: String, colorCode: Int,font: Typeface)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddTextDialogBinding.inflate(inflater, container, false)
        typeface = Typeface.createFromAsset(requireContext().assets, "roboto_regular.ttf")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        binding.inputTextEdittext.requestFocus()

        mInputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //Setup the color picker for text color
        binding.colorPickerRecyclerview.apply {
            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            val colorPickerAdapter = ColorPickerAdapter(requireContext(),this@AddTextDialogFragment)
            colorPickerAdapter.setList(getColors(requireContext()))
            adapter = colorPickerAdapter
        }

        binding.fontPickerRecyclerview.apply {
            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            val fontPickerAdapter = FontPickerAdapter(requireContext(),this@AddTextDialogFragment)
            fontPickerAdapter.setList(fontList)
            adapter = fontPickerAdapter
        }

        binding.colorPickerBtn.setOnClickListener {
            binding.fontPickerRecyclerview.visibility = View.GONE
            binding.colorPickerLyt.visibility = View.VISIBLE
        }

        binding.fontBtn.setOnClickListener {
            binding.fontPickerRecyclerview.visibility = View.VISIBLE
            binding.colorPickerLyt.visibility = View.GONE
        }

        val arguments = requireArguments()

        binding.textTextview.text = arguments.getString(EXTRA_INPUT_TEXT)
        mColorCode = arguments.getInt(EXTRA_COLOR_CODE)
        binding.textTextview.setTextColor(mColorCode)
 //       mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        mInputMethodManager.showSoftInput(binding.inputTextEdittext,0)

        //Make a callback on activity when user is done with text editing
        binding.doneBtn.setOnClickListener { onClickListenerView ->
            mInputMethodManager.hideSoftInputFromWindow(onClickListenerView.windowToken, 0)
            dismiss()
            val inputText = binding.textTextview.text.toString()
            val textEditorListener = mTextEditorListener
            if (inputText.isNotEmpty() && textEditorListener != null) {
                textEditorListener.onDone(inputText, mColorCode,typeface)
            }
        }

        binding.inputTextEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called to notify you that the characters within s are about to be replaced with new text
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within s, the text has been changed
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called to notify you that somewhere within s, the text has been changed
                binding.textTextview.text = s.toString()
            }
        })
        //binding.inputTextEdittext.setText(getString(R.string.hello))
        binding.backBtn.setOnClickListener {
            dismiss()
        }
        binding.keyboardBtn.setOnClickListener {
            if (isKeyboardVisible) {
                hideSoftKeyboard()
            } else {
                showSoftKeyboard()
            }
        }

        binding.colorPickerDialog.setOnClickListener {
            ColorPickerDialog.Builder(context)
                .setTitle("ColorPicker")
                .setPreferenceName(" ")
                .setPositiveButton(getString(R.string.confirm),
                    ColorEnvelopeListener { envelope, _ ->
                        run {
                            binding.textTextview.setTextColor(envelope.color)
                            mColorCode = envelope.color
                        }
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(false) // the default value is true.
                .attachBrightnessSlideBar(false) // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show()
        }
        binding.editColor.setOnClickListener {
            ColorPickerDialog.Builder(context)
                .setTitle("ColorPicker")
                .setPreferenceName(" ")
                .setPositiveButton(getString(R.string.confirm),
                    ColorEnvelopeListener {
                            envelope, _ ->
                        run {binding.textTextview.setTextColor(envelope.color)
                            mColorCode = envelope.color }
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(false) // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show()
        }
    }
    private fun showSoftKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.inputTextEdittext, InputMethodManager.SHOW_IMPLICIT)
        isKeyboardVisible = true
    }

    private fun hideSoftKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputTextEdittext.windowToken, 0)
        isKeyboardVisible = false
    }
    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditorListener: TextEditorListener) {
        mTextEditorListener = textEditorListener
    }

    companion object {
        private val TAG: String = AddTextDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String = "",
            @ColorInt colorCode: Int = ContextCompat.getColor(appCompatActivity, R.color.white)
        ): AddTextDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = AddTextDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }
    }

    override fun onColorSelected(colorCode: Int) {
        mColorCode = colorCode
        binding.textTextview.setTextColor(colorCode)
    }
    private fun getColors(context: Context): List<Int> {
        val colorPickerColors = listOf(
            R.color.light_grey,
            R.color.pale_red,
            R.color.soft_green,
            R.color.sky_blue,
            R.color.light_yellow,
            R.color.pale_cyan,
            R.color.pale_pink,
            R.color.soft_black,
            R.color.cream_white,
            R.color.peach,
            R.color.lavender,
            R.color.light_brown,
            R.color.soft_pink,
            R.color.pale_teal,
            R.color.soft_indigo,
            R.color.pale_violet,
            R.color.pale_lime,
            R.color.soft_navy,
            R.color.silver,
            R.color.champagne_gold,
            R.color.light_maroon,
            R.color.olive,
            R.color.pale_lime_green,
            R.color.pale_sky_blue,
            R.color.misty_steel,
            R.color.pale_slate_gray,
            R.color.salmon,
            R.color.pale_red,
            R.color.soft_green,
            R.color.sky_blue,
            R.color.pale_cyan,
            R.color.pale_pink,
            R.color.pale_yellow,
            R.color.silver,
            R.color.light_gray,
            R.color.medium_pink,
            R.color.medium_slate_blue,
            R.color.medium_spring_green,
            R.color.saddle_brown
        )
        return colorPickerColors.map { ContextCompat.getColor(context, it) }
    }

    override fun onFontSelected(position: Int) {
        typeface = Typeface.createFromAsset(requireContext().assets, fontList[position])
        binding.textTextview.typeface = typeface
    }


}