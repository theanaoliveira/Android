package com.example.anacaroline.agenda;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.anacaroline.agenda.dao.AlunoDAO;
import com.example.anacaroline.agenda.modelo.Aluno;

import java.util.List;

public class ListaAlunosActivity extends AppCompatActivity {

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        clickListaAluno();
        addNovoAluno();
        registerForContextMenu(listaAlunos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);
        String site = aluno.getSite();

        itemMenuSite(menu, site);
        itemMenuSMS(menu, aluno);
        itemMenuMapa(menu, aluno);
        itemMenuDeletar(menu, aluno);
    }

    private void itemMenuSMS(ContextMenu menu, Aluno aluno) {
        MenuItem itemSms = menu.add("Enviar SMS");
        Intent intentSms = new Intent(Intent.ACTION_VIEW);
        intentSms.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemSms.setIntent(intentSms);
    }

    private void itemMenuMapa(ContextMenu menu, Aluno aluno) {
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);
    }

    private void addNovoAluno() {
        Button novoAluno = (Button) findViewById (R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v)  {
                Intent intentVaiProFormulario = new Intent (ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiProFormulario);
            }
        });
    }

    private void clickListaAluno() {
        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);

                Intent intentVaiProFormulario = new Intent (ListaAlunosActivity.this, FormularioActivity.class);
                intentVaiProFormulario.putExtra("aluno", aluno);
                startActivity(intentVaiProFormulario);
            }
        });
    }

    private void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);

        List<Aluno> listAlunos = dao.buscaAlunos();
        dao.close();

        ArrayAdapter<Aluno> adapter = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, listAlunos);
        listaAlunos.setAdapter(adapter);
    }

    private void itemMenuDeletar(ContextMenu menu, final Aluno aluno) {
        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.deleta(aluno);
                dao.close();

                carregaLista();

                return false;
            }
        });
    }

    private void itemMenuSite(ContextMenu menu, String site) {
        if (site != "" && site != null){
            MenuItem itemSite = menu.add("Visitar site");
            Intent intentSite = new Intent(Intent.ACTION_VIEW);

            if(!site.startsWith("http"))
                site = "http://" + site;

            intentSite.setData(Uri.parse(site));
            itemSite.setIntent(intentSite);
        }
    }
}
