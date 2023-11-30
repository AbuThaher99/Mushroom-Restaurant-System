package com.example.mushroom;

public class MonthSales {

      private double total;
      private String date;

        public MonthSales(double total, String date) {
            this.total = total;
            this.date = date;
        }


        public double getTotal() {
            return this.total;
        }


        public String getDate() {
            return this.date;
        }


        public void setTotal(double total) {
            this.total = total;
        }


        public void setDate(String date) {
            this.date = date;
        }
}

