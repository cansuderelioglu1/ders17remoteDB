package com.rd;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.Handle;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        // JDBI bağlantı ayarları
        String jdbcUrl = "jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7723273";
        String username = "sql7723273";
        String password = "K92UnkhHwm";

        // JDBI örneğini oluştur
        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password);

        // CRUD işlemleri
        jdbi.useTransaction(handle -> {
            // Tabloyu oluştur (varsa)
            handle.execute("CREATE TABLE IF NOT EXISTS personel (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "ad VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                    "soyad VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                    "yas INT)");

            // Yeni personel ekle (örnek olarak var olan ID'lerle çakışmaması için)
            handle.execute("INSERT INTO personel (ad, soyad, yas) VALUES (?, ?, ?)", "Cansu", "Dereli", 30);
            handle.execute("INSERT INTO personel (ad, soyad, yas) VALUES (?, ?, ?)", "Nil", "Ak", 25);

            // Tüm personelleri listele
            System.out.println("Tüm Personeller:");
            handle.createQuery("SELECT * FROM personel")
                    .mapToBean(Personel.class)
                    .list()
                    .forEach(p -> System.out.println(p.getId() + ": " + p.getAd() + " " + p.getSoyad() + ", " + p.getYas() + " yaş"));

            // Bir personeli bul
            int personelId = 3; // Test etmek istediğiniz id'yi buraya yazın
            Optional<Personel> foundPersonel = handle.createQuery("SELECT * FROM personel WHERE id = :id")
                    .bind("id", personelId)
                    .mapToBean(Personel.class)
                    .findFirst();

            if (foundPersonel.isEmpty()) {
                System.out.println("ID " + personelId + " ile personel bulunamadı.");
            } else {
                Personel personel = foundPersonel.get();
                System.out.println("Bulunan Personel: " + personel.getAd() + " " + personel.getSoyad() + ", " + personel.getYas() + " yaş");
            }

            // Personeli güncelle
            handle.execute("UPDATE personel SET ad = ?, soyad = ?, yas = ? WHERE id = ?", "Ali", "Yilmaz", 31, 3);

            // Güncellenmiş personeli kontrol et
            Optional<Personel> updatedPersonel = handle.createQuery("SELECT * FROM personel WHERE id = :id")
                    .bind("id", 3)
                    .mapToBean(Personel.class)
                    .findFirst();

            if (updatedPersonel.isEmpty()) {
                System.out.println("ID 3 ile güncellenmiş personel bulunamadı.");
            } else {
                Personel personel = updatedPersonel.get();
                System.out.println("Güncellenmiş Personel: " + personel.getAd() + " " + personel.getSoyad() + ", " + personel.getYas() + " yaş");
            }

            // Personeli sil
            handle.execute("DELETE FROM personel WHERE id = ?", 3);
            System.out.println("Personel silindi. Kalan personeller:");
            handle.createQuery("SELECT * FROM personel")
                    .mapToBean(Personel.class)
                    .list()
                    .forEach(p -> System.out.println(p.getId() + ": " + p.getAd() + " " + p.getSoyad() + ", " + p.getYas() + " yaş"));
        });
    }
}
