package com.tutorial;

import javax.swing.*;
import java.io.*;
import java.sql.SQLOutput;
import java.time.Year;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner terminalInput = new Scanner(System.in);
        String pilihanUser;
        boolean islanjutkan =true;

        while(islanjutkan) {
            clearScreen();
            System.out.println("Database Perpustakaan\n");
            System.out.println("1.\t Lihat Seluruh Buku");
            System.out.println("2.\t Cari Data Buku");
            System.out.println("3.\t Tambah Data Buku");
            System.out.println("4.\t Ubah Data Buku");
            System.out.println("5.\t Hapus Data Buku");

            System.out.print("\n\n Pilihan Anda : ");
            pilihanUser = terminalInput.next();

            switch (pilihanUser) {
                case "1":
                    System.out.println("\n=================");
                    System.out.println("LIST SELURUH BUKU");
                    System.out.println("=================");
                    tampilkanData();
                    break;
                case "2":
                    System.out.println("\n==============");
                    System.out.println("CARI DATA BUKU");
                    System.out.println("==============");
                    cariData();
                    break;
                case "3":
                    System.out.println("\n================");
                    System.out.println("TAMBAH DATA BUKU");
                    System.out.println("================");
                    tambahData();
                    tampilkanData();
                    break;
                case "4":
                    System.out.println("\n==============");
                    System.out.println("UBAH DATA BUKU");
                    System.out.println("==============");
                    updateData();
                    break;
                case "5":
                    System.out.println("\n===============");
                    System.out.println("HAPUS DATA BUKU");
                    System.out.println("===============");
                    deleteData();
                    break;
                default:
                    System.err.println("\n INPUT ANDA TIDAK DI TEMUKAN \n SILAHKAN PILIH (1-5)");
            }
            islanjutkan = getYessorNo("Apakah Anda Ingin Melanjutkan!");
        }

    }
    private static void updateData() throws IOException{
        // kita ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDD = new File("tempDD.txt");
        FileWriter fileOutput = new FileWriter(tempDD);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan data
        System.out.println("List Buku");
        tampilkanData();

        // ambil user input / pilihan data
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan nomor buku yang akan diupdate: ");
        int updateNum = terminalInput.nextInt();

        // tampilkan data yang ingin diupdate

        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while (data != null){
            entryCounts++;

            StringTokenizer st = new StringTokenizer(data,",");

            // tampilkan entrycounts == updateNum
            if (updateNum == entryCounts){
                System.out.println("\nData yang ingin anda update adalah:");
                System.out.println("---------------------------------------");
                System.out.println("Referensi           : " + st.nextToken());
                System.out.println("Tahun               : " + st.nextToken());
                System.out.println("Penulis             : " + st.nextToken());
                System.out.println("Penerbit            : " + st.nextToken());
                System.out.println("Judul               : " + st.nextToken());

                // update data

                // mengambil input dari user

                String[] fieldData = {"tahun","penulis","penerbit","judul"};
                String[] tempData = new String[4];

                st = new StringTokenizer(data,",");
                String originalData = st.nextToken();

                for(int i=0; i < fieldData.length ; i++) {
                    boolean isUpdate = getYessorNo("apakah anda ingin merubah " + fieldData[i]);
                    originalData = st.nextToken();
                    if (isUpdate){
                        //user input

                        if (fieldData[i].equalsIgnoreCase("tahun")){
                            System.out.print("masukan tahun terbit, format=(YYYY): ");
                            tempData[i] = ambilTahun();
                        } else {
                            terminalInput = new Scanner(System.in);
                            System.out.print("\nMasukan " + fieldData[i] + " baru: ");
                            tempData[i] = terminalInput.nextLine();
                        }

                    } else {
                        tempData[i] = originalData;
                    }
                }

                // tampilkan data baru ke layar
                st = new StringTokenizer(data,",");
                st.nextToken();
                System.out.println("\nData baru anda adalah ");
                System.out.println("---------------------------------------");
                System.out.println("Tahun               : " + st.nextToken() + " --> " + tempData[0]);
                System.out.println("Penulis             : " + st.nextToken() + " --> " + tempData[1]);
                System.out.println("Penerbit            : " + st.nextToken() + " --> " + tempData[2]);
                System.out.println("Judul               : " + st.nextToken() + " --> " + tempData[3]);


                boolean isUpdate = getYessorNo("apakah anda yakin ingin mengupdate data tersebut");

                if (isUpdate){

                    // cek data baru di database
                    boolean isExist = cekBukuDiDatabase(tempData,false);

                    if(isExist){
                        System.err.println("data buku sudah ada di database, proses update dibatalkan, \nsilahkan delete data yang bersangkutan");
                        // copy data
                        bufferedOutput.write(data);

                    } else {

                        // format data baru kedalam database
                        String tahun = tempData[0];
                        String penulis = tempData[1];
                        String penerbit = tempData[2];
                        String judul = tempData[3];

                        // kita bikin primary key
                        long nomorEntry = ambilEntryPerTahun(penulis, tahun) + 1;

                        String punulisTanpaSpasi = penulis.replaceAll("\\s+","");
                        String primaryKey = punulisTanpaSpasi+"_"+tahun+"_"+nomorEntry;

                        // tulis data ke database
                        bufferedOutput.write(primaryKey + "," + tahun + ","+ penulis +"," + penerbit + ","+judul);
                    }
                } else {
                    // copy data
                    bufferedOutput.write(data);
                }
            } else {
                // copy data
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();

            data = bufferedInput.readLine();
        }

        // menulis data ke file
        bufferedOutput.flush();

        // delete original database
        database.delete();
        // rename file tempDB menjadi database
        tempDD.renameTo(database);

    }
    private static void deleteData() throws IOException{
        // kita ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan data
        System.out.println("List Buku");
        tampilkanData();

        // kita ambil user input untuk mendelete data
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan nomor buku yang akan dihapus: ");
        int deleteNum = terminalInput.nextInt();

        // looping untuk membaca tiap data baris dan skip data yang akan didelete

        boolean isFound = false;
        int entryCounts = 0;

        String data = bufferedInput.readLine();

        while (data != null){
            entryCounts++;
            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data,",");

            // tampilkan data yang ingin di hapus
            if (deleteNum == entryCounts){
                System.out.println("\nData yang ingin anda hapus adalah:");
                System.out.println("-----------------------------------");
                System.out.println("Referensi       : " + st.nextToken());
                System.out.println("Tahun           : " + st.nextToken());
                System.out.println("Penulis         : " + st.nextToken());
                System.out.println("Penerbit        : " + st.nextToken());
                System.out.println("Judul           : " + st.nextToken());

                isDelete = getYessorNo("Apakah anda yakin akan menghapus?");
                isFound = true;
            }

            if(isDelete){
                //skip pindahkan data dari original ke sementara
                System.out.println("Data berhasil dihapus");
            } else {
                // kita pindahkan data dari original ke sementara
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if(!isFound){
            System.err.println("Buku tidak ditemukan");
        }

        // menulis data ke file
        bufferedOutput.flush();
        // delete original file
        database.delete();
        // rename file sementara ke database
        tempDB.renameTo(database);
    }
    private static void tampilkanData() throws IOException{

        FileReader fileInput;
        BufferedReader bufferInput;

        try{
            fileInput = new FileReader("database.txt");
            bufferInput = new BufferedReader(fileInput);
        }catch (Exception e){
            System.err.println("DATA TIDAK DITEMUKAN!");
            System.err.println("SILAHKAN TAMBAH DATA TERLEBIH DAHULU");
            System.out.println();
            tambahData();
            return;
        }

        System.out.println("\n| NO |\tTahun |\tPenulis               |\tPenerbit             |\tJudul Buku           |");
        System.out.println("--------------------------------------------------------------------------------------");

        String data = bufferInput.readLine();
        int nomorData =0;
        while(data != null) {
            nomorData++;
            StringTokenizer stringToken = new StringTokenizer(data, ",");

            stringToken.nextToken();
            System.out.printf("| %2d ", nomorData);
            System.out.printf("|\t%4s ", stringToken.nextToken());
            System.out.printf(" |\t%-20s  ", stringToken.nextToken());
            System.out.printf("|\t%-20s ", stringToken.nextToken());
            System.out.printf("|\t%-20s |", stringToken.nextToken());
            System.out.println("\n");

            data=bufferInput.readLine();
        }
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println("akhir dari database");
    }
    private static void cariData() throws IOException{

        // membaca data ada atau tidak
        try{
            File file = new File("database.txt");

        }catch (Exception e){
            System.err.println("DATA TIDAK DITEMUKAN!");
            System.err.println("SILAHKAN TAMBAH DATA TERLEBIH DAHULU");
            tambahData();
            return;
        }

        //kita ambil keyword dari user
        Scanner terminalInput = new Scanner(System.in);
        System.out.println("masukan Kata Kunci Untuk Mencari Buku : ");
        String cariString = terminalInput.nextLine();
        String[] keyword = cariString.split("\\s+");

        //kita cek keyword di database

        cekBukuDiDatabase(keyword,true);
    }
    private static void tambahData() throws IOException{
        //mengambil input dari user
        FileWriter fileOutput = new FileWriter("database.txt",true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

        Scanner terminalInput = new Scanner(System.in);
        String penulis,judul,penerbit,tahun;

        System.out.print("Masukan Nama penulis  : ");
        penulis = terminalInput.nextLine();
        System.out.print("Masukan Judul BUku    : ");
        judul = terminalInput.nextLine();
        System.out.print("Masukan Nama Penerbit : ");
        penerbit = terminalInput.nextLine();
        System.out.print("Masukan Tahun Terbit  : ");
        tahun = ambilTahun();

        //cek buku di database

        String[] keyword = {tahun+","+penulis+","+penerbit+","+judul};
        System.out.println(Arrays.toString(keyword));

        boolean isExist =cekBukuDiDatabase(keyword,false);

        //menulis buku di database
        if (!isExist){
          //fiersabesari_2012_1,2012,fiersa besari,media kita,jejak langkah
            System.out.println(ambilEntryPerTahun(penulis, tahun));
            long nomorEntry=ambilEntryPerTahun(penulis, tahun) +1;
            String penulisSanpaSepasi =penulis.replaceAll("\\s+","");
                String primaryKey = penulisSanpaSepasi+"_"+tahun+"_"+nomorEntry;
                System.out.println("\n Data Yang Anda Masukan Adalah");
                System.out.println("-----------------------------------");
                System.out.println("Primary key  : " +primaryKey);
                System.out.println("Tahun Terbit : " +tahun);
                System.out.println("Penulis      : " +penulis);
                System.out.println("Penerbit     : " +penerbit);

                boolean isTambah = getYessorNo("Apakah Anda Ingin Menambah Data ");

                //masukan data yang baru ke database
                if(isTambah){
                    bufferOutput.write(primaryKey+","+tahun+","+penulis+","+penerbit+","+judul);
                    bufferOutput.newLine();
                    bufferOutput.flush();
                }

        }else{
            System.out.println("Buku yang anda masukan sudah tersedia di database dengan data sebagai berikut :");
            cekBukuDiDatabase(keyword,true);
        }
        bufferOutput.close();
    }
    private static long ambilEntryPerTahun(String penulis, String tahun) throws IOException{
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        long entry = 0;
        String data = bufferInput.readLine();
        Scanner dataScanner;
        String primaryKey;

        while(data != null){
            dataScanner = new Scanner(data);
            dataScanner.useDelimiter(",");
            primaryKey = dataScanner.next();
            dataScanner = new Scanner(primaryKey);
            dataScanner.useDelimiter("_");

            penulis = penulis.replaceAll("\\s+","");

            if (penulis.equalsIgnoreCase(dataScanner.next()) && tahun.equalsIgnoreCase(dataScanner.next())){
                entry = dataScanner.nextInt();
            }

            data = bufferInput.readLine();
        }
        return entry;

    }
    private static boolean cekBukuDiDatabase(String [] keyword, boolean isDisplay) throws IOException{

        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        String data = bufferInput.readLine();
        boolean isExist = false;
        int nomorData =0;

        if (isDisplay) {
            System.out.println("\n| NO |\tTahun |\tPenulis               |\tPenerbit             |\tJudul Buku           |");
            System.out.println("--------------------------------------------------------------------------------------");
        }
        while(data != null){

            // cek keyword didalam baris
            isExist = true;

            for (String keywords : keyword) {
                isExist = isExist && data.toLowerCase().contains(keywords.toLowerCase());

            }
            //jika keywordnya cocok maka tampilkan
            if (isExist){
                if (isDisplay) {
                    nomorData++;
                    StringTokenizer stringToken = new StringTokenizer(data, ",");

                    stringToken.nextToken();
                    System.out.printf("| %2d ", nomorData);
                    System.out.printf("|\t%4s ", stringToken.nextToken());
                    System.out.printf(" |\t%-20s  ", stringToken.nextToken());
                    System.out.printf("|\t%-20s ", stringToken.nextToken());
                    System.out.printf("|\t%-20s |", stringToken.nextToken());
                    System.out.println("\n");
                }else {
                    break;
                }
            }
            data = bufferInput.readLine();
        }
        if(isDisplay) {
            System.out.println("--------------------------------------------------------------------------------------");
        }
        return isExist;
    }
    private static String ambilTahun() throws IOException{
        boolean tahunvalid = false;
        Scanner terminalInput = new Scanner(System.in);
        String tahunInput= terminalInput.nextLine();
        while(!tahunvalid) {
            try {
                Year.parse(tahunInput);
                tahunvalid = true;
            } catch (Exception e) {
                System.out.println("FORMAT TAHUN YANG ANDA MASUKAN SALAH! (YYYY)");
                System.out.print("\nSilahkan Masukan Tahun Lagi  : ");
                tahunvalid = false;
                tahunInput = terminalInput.nextLine();
            }
        }
        return tahunInput;
    }
    private static boolean getYessorNo(String massage){
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n"+massage+" (y/n)? ");
        String pilihanUser = terminalInput.next();

        while (!pilihanUser.equalsIgnoreCase("y")&& !pilihanUser.equalsIgnoreCase("n")){
            System.err.println("PILIHAN ANDA BUKAN Y ATAU N");
            System.out.print("\n"+massage+" (y/n)? ");
            pilihanUser = terminalInput.next();
        }
        return pilihanUser.equalsIgnoreCase("y");
    }

    private static void  clearScreen(){
        try{
            if (System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }else {
                System.out.print("\003\143");
            }
        }catch (Exception ex){
            System.err.println("TIDAK BISA CLEAR SCREEN");
        }
    }
}
