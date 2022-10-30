import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;



public class ServerImpl extends UnicastRemoteObject implements ServerIntf {

    ArrayList<Reservas> listaReservas = new ArrayList<>();
    ArrayList<Reservas> listaReservasParaVerificacoes = new ArrayList<>();
   
    //VARIAVEIS GLOBAIS PARA EFEITO DE LOGS CORRETOS NO SERVIDOR 
    public static int idParaFile = 1;
    public static boolean erroMesas = false;

    //Quando se liga o servidor
    public ServerImpl() throws RemoteException, Exception {

        System.out.println("Server turning ON\nGetting Data...");

        lerDados();

        for (int i = 0; i < listaReservas.size(); i++) {
            System.out.println(listaReservas.get(i));
        }
        System.out.println("separar");
        for (int i = 0; i < listaReservasParaVerificacoes.size(); i++) {
            System.out.println(listaReservasParaVerificacoes.get(i));
        }

        System.out.println("Server Ready");
    }

    public void saveDados(String dataMarcacao, String escolhaRefeicao, int numeroDePessoas) throws RemoteException, Exception {

        //GUARDA DADOS NA BASE DE DADOS (Ficheiro TXT)
        try {  
            /* 
            //SABER QUANTAS LINHAS TEM O FILE
            FileReader fr = new FileReader("BaseDeDados.txt");
            BufferedReader reader = new BufferedReader(fr);
    
            String linha = null;
            
            while ((linha = reader.readLine()) != null) {
                numeroDaLinha++;
            }

            reader.close();
            */
            File file = new File("BaseDeDados.txt");
            //Argumento TRUE para que dê append no ficheiro e não apague registos antigos
            FileWriter writer = new FileWriter(file,true);
            PrintWriter write = new PrintWriter(writer);


            Reservas a = new Reservas(idParaFile,dataMarcacao, escolhaRefeicao, numeroDePessoas);
            
            //listaReservas = listaReservasParaVerificacoes;
            listaReservasParaVerificacoes.add(a); 
            
            System.out.println("CHEGUEI 1");
            //CHAMADA FUNCAO QUE VERIFICA SE PODE REALIZAR A MARCACAO OU NAO
            marcarMesa(dataMarcacao,escolhaRefeicao, numeroDePessoas);
            System.out.println("CHEGUEI 2");
            
            if(!mesaCodeErro()){
                write.println(idParaFile + "@" + dataMarcacao + "@" + escolhaRefeicao + "@" + numeroDePessoas);
            }
            
           
            /* /
            if(id == 1){
                write.println(id + "@" + dataMarcacao + "@" + escolhaRefeicao);
            }else{
                write.println(id + "@" + dataMarcacao + "@" + escolhaRefeicao);
            }
            
            
            //FORMA COMO GUARDAR: ID - DIA DA RESERVA - ALMOÇO/JANTAR
            /* 
            for (int id = numeroDaLinha+1; id < listaReservas.size()+1 ; id++) {
                if(verificaSeJaExiste(id)){
                    continue;
                }else{
                    write.append(id + "@" + dataMarcacao + "@" + escolhaRefeicao + "\n");                
                    idParaLogServer = id;
                }

                
                
            
                if(id < 1){
                    write.println(1 + "@" + dataMarcacao + "@" + escolhaRefeicao);
                    idParaLogServer = 1;
                }else{
                    write.println();
                    write.println(id + "@" + dataMarcacao + "@" + escolhaRefeicao);                
                    idParaLogServer = id;
                }
                
            }  
            */
 
            System.out.println("Nova Reserva registada no servidor/BD com ID = " + idParaFile);

            write.close();            
            
        } catch (Exception e) {
            throw new Exception("ERRO AO GRAVAR FICHEIRO"); 
        }

    }

    public void lerDados() throws RemoteException,Exception {


        try {   
            
            FileReader fr = new FileReader("BaseDeDados.txt");
            BufferedReader reader = new BufferedReader(fr);
    
            String linha = null;
            
            while ((linha = reader.readLine()) != null) {
    
                String[] dados = linha.split("@");

                int id = Integer.parseInt(dados[0]);
                String data = dados[1];
                String escolhaRefeicao  = dados[2];
                int  numeroDePessoas  = Integer.parseInt(dados[3]);
    
                Reservas reserva = new Reservas(id, data, escolhaRefeicao,numeroDePessoas );
                listaReservas.add(reserva);

            }

            reader.close();
            fr.close();
         

        } catch (Exception e) {
            throw new Exception("ERRO AO TRANSFERIR DADOS DA BASE DE DADOS"); 
  
        }
    }

    public void marcarMesa(String DataInserida, String jantarOUalmocoInserido, int quantidadeDePessoas) throws RemoteException {
    
        int contadorDeMesas = 0;
        
        for (int i = 0; i < listaReservasParaVerificacoes.size(); i++) {
           
            if(listaReservasParaVerificacoes.get(i).getData().equals(DataInserida) 
                && listaReservasParaVerificacoes.get(i).getEscolhaRefeicao().equals(jantarOUalmocoInserido)
                && listaReservasParaVerificacoes.get(i).getNumeroDePessoas() == 2){

                contadorDeMesas++;
                idParaFile = contadorDeMesas; 
               
                if(contadorDeMesas > 10){
                    erroMesas = true;
                    mesaCodeErro();
                } else{
                    Reservas reservaValida = new Reservas(idParaFile, DataInserida, jantarOUalmocoInserido, quantidadeDePessoas);
                    listaReservas.add(reservaValida);
                }
            }

            if(listaReservasParaVerificacoes.get(i).getData().equals(DataInserida) 
                && listaReservasParaVerificacoes.get(i).getEscolhaRefeicao().equals(jantarOUalmocoInserido)
                && listaReservasParaVerificacoes.get(i).getNumeroDePessoas() == 4){

                contadorDeMesas++;
                idParaFile = contadorDeMesas; 

                if(contadorDeMesas > 5){
                    erroMesas = true;
                    mesaCodeErro();
                } else{
                    Reservas reservaValida = new Reservas(contadorDeMesas, DataInserida, jantarOUalmocoInserido, quantidadeDePessoas);
                    listaReservas.add(reservaValida);
                }  
            }

            if(listaReservasParaVerificacoes.get(i).getData().equals(DataInserida) 
                && listaReservasParaVerificacoes.get(i).getEscolhaRefeicao().equals(jantarOUalmocoInserido)
                && listaReservasParaVerificacoes.get(i).getNumeroDePessoas() == 8){

                contadorDeMesas++;
                idParaFile = contadorDeMesas; 

                if(contadorDeMesas > 5){
                    erroMesas = true;
                    mesaCodeErro();
                } else{
                    Reservas reservaValida = new Reservas(contadorDeMesas, DataInserida, jantarOUalmocoInserido, quantidadeDePessoas);
                    listaReservas.add(reservaValida);
                }  
            } 

            if(listaReservasParaVerificacoes.get(i).getData().equals(DataInserida) 
                && listaReservasParaVerificacoes.get(i).getEscolhaRefeicao().equals(jantarOUalmocoInserido)
                && listaReservasParaVerificacoes.get(i).getNumeroDePessoas() == 12){
            
                contadorDeMesas++;
                idParaFile = contadorDeMesas; 

                if(contadorDeMesas > 5){
                    erroMesas = true;
                    mesaCodeErro();
                } else{
                    Reservas reservaValida = new Reservas(contadorDeMesas, DataInserida, jantarOUalmocoInserido, quantidadeDePessoas);
                    listaReservas.add(reservaValida);
                }  
            }

        }

    }

    public boolean mesaCodeErro() throws RemoteException {
        return erroMesas;
    }
        


}
