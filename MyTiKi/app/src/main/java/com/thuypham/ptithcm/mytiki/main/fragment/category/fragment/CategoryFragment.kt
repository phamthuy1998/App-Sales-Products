package com.thuypham.ptithcm.mytiki.main.fragment.category.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.category.adapter.CategoryAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Category
import kotlinx.android.synthetic.main.category_fragment.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.loading_layout.*


class CategoryFragment : Fragment() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Category
    private var adapter: CategoryAdapter? = null
    private var categoryList = ArrayList<Category>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.category_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inIt()

        getDataCategory()

//
//        mDatabaseReference = mDatabase!!.reference
//        val currentUserDb = mDatabaseReference!!.child(PhysicsConstants.PRODUCT).push()
//        currentUserDb.child(PhysicsConstants.PRODUCT_ID).setValue(id)
//        currentUserDb.child(PhysicsConstants.NAME_PRODUCT).setValue("Tai Nghe Nhét Tai Mi Basic Xiaomi HSEJ03JY - Hàng Chính Hãng")
//        currentUserDb.child(PhysicsConstants.PRICE_PRODUCT).setValue(102999)
//        currentUserDb.child(PhysicsConstants.IMAGE_PRODUCT).setValue("https://salt.tikicdn.com/cache/200x200/ts/product/6a/8d/4f/f3a5f3ebd9b2e69406821f922cfcc537.jpg")
//        currentUserDb.child(PhysicsConstants.INFOR_PRODUCT).setValue("Tần số: 20 - 20.000 Hz\n" +
//                "\n" +
//                "Trở kháng: 32 ohm\n" +
//                "\n" +
//                "Lõi dây bằng đồng tráng men\n" +
//                "\n" +
//                "Nút nhét làm bằng silicon mềm mại\n" +
//                "\n" +
//                "Độ phân giải cao cho chất lượng âm thanh chính xác")
//        currentUserDb.child(PhysicsConstants.PRODUCT_COUNT).setValue(10)
//        currentUserDb.child(PhysicsConstants.ID_CATEGORY_PRODUCT).setValue("-Lmwzjdledim2IDvaAy-")

        addEvent()
    }

    private fun addEvent() {
        gv_category.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Get the GridView selected/clicked item text
                val selectedItem = parent.getItemAtPosition(position).toString()

                // Display the selected/clicked item text and position on TextView
                Toast.makeText(
                        requireContext(),
                        "GridView item clicked : ${categoryList[position].nameCategory} \\nAt index position : $position\"",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getDataCategory() {
        progress.visibility = View.VISIBLE
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryList.clear()
                for (ds in dataSnapshot.children) {
                    val id = ds.child(PhysicsConstants.CATEGORY_ID).value as String
                    val name = ds.child(PhysicsConstants.CATEGORY_NAME).value as String
                    val image = ds.child(PhysicsConstants.CATEGORY_IMAGE).value as String
                    val count = ds.child(PhysicsConstants.CATEGORY_COUNT).value as Long
                    println("lay du lieu ten $name")
                    println("lay du lieu  anh$image")
                    println("lay du lieu so $count")
                    println("lay du lieu id $id")

                    val category = Category(id, name, image, count)
                    categoryList.add(category)

                }
                adapter?.notifyDataSetChanged()

                progress.visibility = View.GONE

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                        requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                        Toast.LENGTH_LONG
                ).show()
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference.addValueEventListener(valueEventListener)
    }


    private fun inIt() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child(PhysicsConstants.CATEGORY_table)
        // create adapter
        adapter = CategoryAdapter(requireContext(), categoryList)
        gv_category.adapter = adapter
    }
}