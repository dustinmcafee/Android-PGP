package net.kibotu.pgp.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.pgp.Pgp


class MainActivity : AppCompatActivity() {

    private val TAG: String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setKeysFromAssets()

        decrypted.setText("decrypted.json".stringFromAssets())
        encrypted.setText("encrypted.txt".stringFromAssets())

        encrypt.setOnClickListener {
            val decryptedText = decrypted.text.toString().trim()
            if (!isEmpty(decryptedText))
                encrypted.setText(Pgp.encrypt(decryptedText, false))
        }

        decrypt.setOnClickListener {
            val encryptedText = encrypted.text.toString().trim()
            if (!isEmpty(encryptedText)) {
                try {
                    val decryptedText = Pgp.decrypt(encryptedText, "play2t", false)
                    decrypted.setText(decryptedText)
                } catch (e: Exception) {
                    val t = Toast.makeText(this@MainActivity, "Could not decrypt message: " + e, Toast.LENGTH_LONG)
                    e.printStackTrace()
                    t.show()
                }
            }
        }

        share.setOnClickListener { share() }
        delete.setOnClickListener {
            encrypted.setText("")
            decrypted.setText("")
        }
    }

    private fun setKeysFromAssets() {
        Pgp.setPublicKey("rsa.pub".stringFromAssets())
        Pgp.setPrivateKey("rsa".stringFromAssets())
    }

    private fun share() {
        share(decrypted.text.toString().trim() + "\n\n" + encrypted.text.toString().trim())
    }

    private fun share(message: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "" + message)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Save Data with:"))
    }

    fun String.bytesFromAssets(): ByteArray? = try {
        assets.open(this).use { ByteArray(it.available()).apply { it.read(this) } }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun String.stringFromAssets(): String = try {
        assets.open(this).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
