package com.midasgo.bookdoc.view.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.midasgo.bookdoc.MyApp
import com.midasgo.bookdoc.R
import com.midasgo.bookdoc.com.midasgo.bookdoc.common.Constant
import com.midasgo.bookdoc.com.midasgo.bookdoc.util.Util
import com.midasgo.bookdoc.databinding.ActModifyBookBinding
import com.midasgo.bookdoc.model.entity.BookEntity
import com.midasgo.bookdoc.structure.book
import com.midasgo.bookdoc.viewmodel.BookViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.act_book_detail.*
import java.util.*
import java.util.concurrent.TimeUnit


/*
책 추가화면
 */
class ActModifyBook : AppCompatActivity() {
    /****************** member ******************/
    private lateinit var binding: ActModifyBookBinding
    private var mContext:Context ?= null
    private var mApp: MyApp?= null
    private var mBookInfo: book = book()
    /****************** system function ******************/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.act_modify_book)

        mContext = this
        mApp = application as MyApp
        if(mApp?.m_bInit == false)
            mApp?.init(mContext!!)

        initValue()
        recvIntentData()
        initLayout()
    }
    /****************** user funtion ******************/
    //-------------------------------------------
    //
    private fun initValue(){
    }
    //-------------------------------------------
    //
    private fun recvIntentData(){
        mBookInfo = intent.getParcelableExtra(Constant.INTENT_DATA_BOOK_INFO)
    }
    //-------------------------------------------
    //
    private fun initLayout(){
        setEventListener()
        setRxBinding()

        if(mBookInfo != null)
        {
            binding.editTitle.setText(mBookInfo.title)
            binding.editDesc.setText(mBookInfo.desc)
            binding.editReadPage.setText(mBookInfo.read_page)
            binding.editTotalPage.setText(mBookInfo.total_page)

            var date:String? = Util.getDateFromTimestamp(mBookInfo.reg_date.toLong(), "yyyy-MM-dd")
            var array: List<String> = date!!.split("-")
            var year:Int = array[0].toInt()
            var month:Int = array[1].toInt()
            var day:Int = array[2].toInt()
            binding.datePicker.updateDate(year,month-1,day)
        }

    }
    //---------------------------------------------------
    //
    fun setEventListener(){
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    //-------------------------------------------
    //
    private fun setRxBinding(){
        //RxBinding UIEvent

        //타이틀 입력
        binding.editTitle
            .textChanges()
            .subscribe {
                if(!it.toString().equals(""))
                    mBookInfo.title = it.toString()
            }

        //내용입력
        binding.editDesc
            .textChanges()
            .subscribe(){
                if(!it.toString().equals(""))
                    mBookInfo.desc = it.toString()
            }

        //읽은페이지 입력
        binding.editReadPage
            .textChanges()
            .subscribe() {
                if(!it.toString().equals(""))
                    mBookInfo.read_page = it.toString()
            }

        //총 페이지
        binding.editTotalPage
            .textChanges()
            .subscribe() {
                if(!it.toString().equals(""))
                    mBookInfo.total_page =it.toString()
            }

        //수정 완료 버튼
        binding.btnComplete.clicks()
            .debounce(300, TimeUnit.MILLISECONDS)//중복클릭 방지
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(checkValidation()){
                    var viewModel: BookViewModel = ViewModelProviders.of(this).get(
                        BookViewModel::class.java)
                    viewModel.update(BookEntity.getEntity(mBookInfo))
                    mApp!!.goMain(mContext!!)
                }
                else
                {

                }
            })
    }
    //-------------------------------------------
    //책 데이터 입력 값 유효성검사
    private fun checkValidation():Boolean{
        var bResult:Boolean = false
        if(mBookInfo.title.equals(""))
        {
            Toast.makeText(mContext!!, mContext!!.resources.getString(R.string.input_title_msg), Toast.LENGTH_SHORT).show()
        }
        else if(mBookInfo.desc.equals("")){
            Toast.makeText(mContext!!, mContext!!.resources.getString(R.string.input_desc_msg), Toast.LENGTH_SHORT).show()
        }
        else if(mBookInfo.read_page.equals(""))
        {
            Toast.makeText(mContext!!, mContext!!.resources.getString(R.string.input_read_page_msg), Toast.LENGTH_SHORT).show()
        }
        else if(mBookInfo.total_page.equals(""))
        {
            Toast.makeText(mContext!!, mContext!!.resources.getString(R.string.input_total_page_msg), Toast.LENGTH_SHORT).show()
        }
        else{
            bResult = true
        }

        var date: Date = Util.getDateFromDatePicker(binding.datePicker)
        mBookInfo.reg_date = String.format("%s",date.time)
        return bResult
    }
}
