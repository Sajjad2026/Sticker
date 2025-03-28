package com.nova.pack.stickers.activites

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.jeluchu.jchucomponents.ktx.any.isNull
import com.nova.pack.stickers.R
import com.nova.pack.stickers.utils.DrawView
import com.nova.pack.stickers.viewmodel.EditImageViewModel
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
import dagger.hilt.android.AndroidEntryPoint
import top.defaults.checkerboarddrawable.CheckerboardDrawable

import java.io.IOException
import java.lang.Exception

@AndroidEntryPoint
class CutOutActivity : AppCompatActivity() {
    private var viewModel: EditImageViewModel?=null

    private val MAX_ZOOM = 4f

    lateinit var back_btn:ImageView
    lateinit var redo:ImageView
    lateinit var undo:ImageView
    lateinit var apply:ImageView
    lateinit var auto_img:ImageView
    lateinit var eraser_img:ImageView
    lateinit var magic_img:ImageView
    lateinit var loadingModal: FrameLayout
    //lateinit var gestureView: GestureView
    lateinit var drawViewLayout: FrameLayout
    lateinit var drawView: DrawView
    lateinit var auto:LinearLayout
    lateinit var auto_txt:TextView
    lateinit var eraser:LinearLayout
    lateinit var eraser_text:TextView
    lateinit var magic:LinearLayout
    lateinit var magic_txt:TextView
    lateinit var seek_layout:RelativeLayout
    lateinit var seekbar:SeekBar
    lateinit var loader:RelativeLayout
    var autoeraseimage:Bitmap?=null
    var toolCheck=""
    var bitmap:Bitmap?=null
    var isBackPressed=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[EditImageViewModel::class.java]
        setContentView(R.layout.activity_cut_out)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {
            bitmap = if (viewModel?.editedImage?.value.isNull()) {
                viewModel?.originalImage?.value
            } else {
                viewModel?.editedImage?.value
            }

            FindId()
            SetupViews()
            initializeActionButtons()
            setUndoRedo()
            start()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        /*  if (!OpenCVLoader.initDebug()) {
          } else {
          }*/
    }

    private fun ActiveTool(txt1:TextView,txt2:TextView,txt3:TextView,img1:ImageView,img2:ImageView,img3:ImageView){
        txt1.setTextColor(resources.getColor(R.color.bg_color))
        txt2.setTextColor(resources.getColor(R.color.grey))
        txt3.setTextColor(resources.getColor(R.color.grey))
        val colorStateList = ColorStateList.valueOf(resources.getColor(R.color.bg_color))
        val colorStateList2 = ColorStateList.valueOf(resources.getColor(R.color.grey))
        img1.backgroundTintList= colorStateList
        img2.backgroundTintList=colorStateList2
        img3.backgroundTintList=colorStateList2
    }

    private fun SetupViews() {
        drawViewLayout = findViewById(R.id.drawViewLayout)
        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            drawViewLayout.setBackgroundDrawable(CheckerboardDrawable.create())
        } else {
            drawViewLayout.background = CheckerboardDrawable.create()
        }

        drawView.isDrawingCacheEnabled = true
        drawView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        //drawView.setDrawingCacheEnabled(true);
        drawView.setStrokeWidth(50)

        loadingModal.setVisibility(View.INVISIBLE)

        drawView.setLoadingModal(loadingModal)

        apply.setOnClickListener {
            startSaveDrawingTask()
        }

        back_btn.setOnClickListener { finish()
            /// Thread.currentThread().interrupt();
            if (!isBackPressed) {
                isBackPressed = true
                Back()
            }
        }

    }

    private fun FindId() {
        back_btn=findViewById(R.id.back_btn)
        redo=findViewById(R.id.redo)
        undo=findViewById(R.id.undo)
        apply=findViewById(R.id.apply)
        auto=findViewById(R.id.auto)
        auto_txt=findViewById(R.id.auto_txt)
        eraser=findViewById(R.id.eraser)
        eraser_text=findViewById(R.id.eraser_text)
        magic=findViewById(R.id.magic)
        magic_txt=findViewById(R.id.magic_txt)
//          gestureView = findViewById(R.id.gestureView)
        drawView = findViewById(R.id.drawView)
        loadingModal = findViewById(R.id.loadingModal)
        seek_layout = findViewById(R.id.seek_layout)
        seekbar = findViewById(R.id.seekbar)
        loader = findViewById(R.id.loader)
        auto_img = findViewById(R.id.auto_img)
        eraser_img = findViewById(R.id.eraser_img)
        magic_img = findViewById(R.id.magic_img)
    }

    private fun start() {
        //setDrawViewBitmap(CroopingScreen.finalbitmap)
        Log.d("Skjsff",bitmap.toString())
        drawView.setBitmap(bitmap)
    }

    private fun startSaveDrawingTask() {
        /* val task = SaveDrawingTask(this)
         var borderColor: Int
         if (intent.getIntExtra(CutOut.CUTOUT_EXTRA_BORDER_COLOR, -1)
                 .also { borderColor = it } != -1
         ) {
             val image: Bitmap = BitmapUtility.getBorderedBitmap(drawView!!.drawingCache, borderColor, BORDER_SIZE)
             task.execute(image)
         } else {
             task.execute(drawView!!.drawingCache)
         }
 */

        Background().execute()

    }

    /* private fun activateGestureView() {
         gestureView!!.controller.settings
             .setMaxZoom(MAX_ZOOM)
             .setDoubleTapZoom(-1f) // Falls back to max zoom level
             .setPanEnabled(true)
             .setZoomEnabled(true)
             .setDoubleTapEnabled(true)
             .setOverscrollDistance(0f, 0f).overzoomFactor = 2f
     }

     private fun deactivateGestureView() {
         gestureView!!.controller.settings
             .setPanEnabled(false)
             .setZoomEnabled(false).isDoubleTapEnabled = false
     }
 */
    private fun initializeActionButtons() {
        magic.isActivated = false
        magic.setOnClickListener { buttonView: View? ->
            toolCheck="magic"
            if (!magic.isActivated) {
                drawView!!.setAction(DrawView.DrawViewAction.AUTO_CLEAR)
                magic.isActivated = true
                eraser.isActivated = false
                seek_layout.visibility=View.GONE
                // zoomButton.isActivated = false
                //deactivateGestureView()
                ActiveTool(magic_txt,eraser_text,auto_txt,magic_img,eraser_img,auto_img)
            }
        }
        eraser.isActivated = false
        eraser.setOnClickListener { buttonView: View? ->
            toolCheck="eraser"
            if (!eraser.isActivated) {
                drawView!!.setAction(DrawView.DrawViewAction.MANUAL_CLEAR)
                eraser.isActivated = true
                magic.isActivated = false
                seek_layout.visibility=View.VISIBLE
                //zoomButton.isActivated = false
                //deactivateGestureView()
                ActiveTool(eraser_text,magic_txt,auto_txt,eraser_img,auto_img,magic_img)
            }
        }

        // deactivateGestureView()


        auto.setOnClickListener {
            toolCheck="auto"
            eraser.isActivated = false
            magic.isActivated = false
            seek_layout.visibility=View.GONE
            drawView!!.setAction(DrawView.DrawViewAction.ZOOM)
            ActiveTool(auto_txt,eraser_text,magic_txt,auto_img,eraser_img,magic_img)
            AutoRemove().execute()
            bitmap?.let { PerfoamAuto(it) }
        //    Toast.makeText(this@CutOutActivity,getString(R.string.comming_soon),Toast.LENGTH_SHORT).show()
        }

        /* zoomButton.isActivated = false
         zoomButton.setOnClickListener { buttonView: View? ->
             if (!zoomButton.isActivated) {
                 drawView!!.setAction(DrawView.DrawViewAction.ZOOM)
                 manualClearSettingsLayout!!.visibility = View.INVISIBLE
                 zoomButton.isActivated = true
                 eraser.isActivated = false
                 magic.isActivated = false
                 activateGestureView()
             }
         }*/

        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, i: Int, b: Boolean) {
                drawView.setStrokeWidth(i)
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {

            }

        })



    }

    private fun PerfoamAuto(bitmap: Bitmap){
        BackgroundRemover.bitmapForProcessing(
            bitmap,false, object: OnBackgroundChangeListener {
                override fun onSuccess(bitmap: Bitmap) {
                    autoeraseimage=bitmap
                    drawView.setBitmap(bitmap)
                    loader.visibility = View.GONE
                }

                override fun onFailed(exception: Exception) {
                    loader.visibility = View.GONE
                }
            }
        )

    }

    private fun setUndoRedo() {
        //val undoButton: Button = findViewById(R.id.undo)
        undo.isEnabled = true
        undo.setOnClickListener { v: View? -> undo() }
        //val redoButton: Button = findViewById(R.id.redo)
        redo.isEnabled = true
        redo.setOnClickListener { v: View? -> redo() }
        drawView!!.setButtons(undo, redo)
    }

    private fun setDrawViewBitmap(uri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            drawView!!.setBitmap(bitmap)
        } catch (e: IOException) {
        }
    }

    private fun undo() {

        if(!toolCheck.equals("auto")){
            drawView!!.undo()
        }else
        {
            drawView.setBitmap(bitmap)
        }
    }

    private fun redo() {

        if(!toolCheck.equals("auto"))
        {
            drawView!!.redo()
        }else
        {
            if(autoeraseimage !=null)
            {
                drawView.setBitmap(autoeraseimage)
            }
            else{
                drawView.setBitmap(bitmap)
            }

        }
    }

    inner class Background() : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            drawViewLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            drawViewLayout.setBackgroundResource(0)
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            viewModel?.editedImage?.postValue(bitmap)
            val activityName = intent.getStringExtra("activityName")
            val intent = Intent(this@CutOutActivity, EditStickerImageActivity::class.java)
            intent.putExtra("activityName", activityName)
            startActivity(intent)
            finish()
        }


        override fun doInBackground(vararg p0: Void?): Void? {
            if (!toolCheck.equals("auto")) {
                Log.d("skjsfs", "auto")
                drawView.isDrawingCacheEnabled = true
                val cachedBitmap = drawView.drawingCache
                if (cachedBitmap != null && !cachedBitmap.isRecycled) {
                    bitmap = Bitmap.createBitmap(cachedBitmap)
                } else {
                    Log.e("BitmapError", "Cached bitmap is null or has been recycled")
                }
                drawView.isDrawingCacheEnabled = false // Disable cache after using it
            } else {
                Log.d("skjsfs", "erase")
                if (autoeraseimage != null && !autoeraseimage!!.isRecycled) {
                    bitmap = Bitmap.createBitmap(autoeraseimage!!)
                } else {
                    Log.e("BitmapError", "Autoerase image is null or has been recycled")
                }
            }
            return null
        }
    }

    inner class AutoRemove : AsyncTask<Void?, Void?, Void?>() {

        /*  lateinit  var python:Python;

          init {
              python= Python.getInstance()

          }
  */
        override fun onPreExecute() {
            loader.visibility = View.VISIBLE

        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun doInBackground(vararg voidArr: Void?): Void? {
            return try {
                null
            } catch (e: java.lang.Exception) {
                null
            }
        }

        override fun onPostExecute(voidR: Void?) {
            loader.visibility = View.GONE
        }



    }

    fun mergeToPinBitmap(bitmap: Bitmap, bitmap2: Bitmap): Bitmap {
        val createBitmap =
            Bitmap.createBitmap(bitmap2.width, bitmap2.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(createBitmap)
        val paint = Paint(1)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null as Paint?)
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint)
        paint.xfermode = null as Xfermode?
        /*bitmap2.recycle()
        bitmap.recycle()
        */return createBitmap
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isBackPressed) {
            isBackPressed = true
            Back()
        }
    }

    private fun Back()
    {
        Thread.currentThread().interrupt();
        finish()
    }

}