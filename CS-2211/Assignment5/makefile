# Makefile for Snackaroo 2.0
CC = gcc
CFLAGS = -Wall -std=c99

snackaroo: snackaroo.o snackaroo_dish.o snackaroo_driver.o snackaroo_dish_driver.o snackaroo_file_io.o
	$(CC) -o snackaroo snackaroo.o snackaroo_dish.o snackaroo_driver.o snackaroo_dish_driver.o snackaroo_file_io.o

snackaroo.o: snackaroo.c snackaroo.h snackaroo_dish.h snackaroo_driver.h snackaroo_dish_driver.h snackaroo_file_io.h
	$(CC) $(CFLAGS) -c snackaroo.c

snackaroo_dish.o: snackaroo_dish.c snackaroo_dish.h snackaroo.h
	$(CC) $(CFLAGS) -c snackaroo_dish.c

snackaroo_driver.o: snackaroo_driver.c snackaroo_driver.h snackaroo.h
	$(CC) $(CFLAGS) -c snackaroo_driver.c

snackaroo_dish_driver.o: snackaroo_dish_driver.c snackaroo_dish_driver.h snackaroo_dish.h snackaroo_driver.h snackaroo.h
	$(CC) $(CFLAGS) -c snackaroo_dish_driver.c

snackaroo_file_io.o: snackaroo_file_io.c snackaroo_file_io.h snackaroo_dish.h snackaroo_driver.h snackaroo_dish_driver.h snackaroo.h
	$(CC) $(CFLAGS) -c snackaroo_file_io.c