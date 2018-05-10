package com.example.anacaroline.agenda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.anacaroline.agenda.dao.AlunoDAO;
import com.example.anacaroline.agenda.modelo.Aluno;

import java.io.File;

public class FormularioActivity extends AppCompatActivity {

    FormularioHelper helper;
    public static final int CODIGO_CAMERA = 124;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        helper = new FormularioHelper(this);

        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

        if (aluno != null){
            helper.preencheFormulario(aluno);
        }

        GetPhotoUser();
    }

    private void GetPhotoUser() {
        Button botaoFoto = (Button) findViewById(R.id.formulario_botaoFoto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               caminhoFoto = getExternalFilesDir(null) + "/foto_"+ System.currentTimeMillis() +".png";
               File arquivoFoto = new File(caminhoFoto);

               intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                       FileProvider.getUriForFile(FormularioActivity.this,BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));

               startActivityForResult(intentCamera, CODIGO_CAMERA);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();
                AlunoDAO dao = new AlunoDAO(this);

                if(aluno.getId() != null){
                    dao.altera(aluno);
                } else {
                    dao.insere(aluno);
                }

                dao.close();

                Toast.makeText(FormularioActivity.this, "Aluno: " + aluno.getNome() + " salvo!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (resultCode){
                case CODIGO_CAMERA:
                    helper.carregaImagem(caminhoFoto);
                    break;
            }
        }
    }
}
