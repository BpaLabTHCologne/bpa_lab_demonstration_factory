import os
import time
import random 

class StoragePlace:
    def __init__(self, _x, _y):
        self.x = _x
        self.y = _y

class Position:
    def __init__(self, _x, _y, _z):
        self.x = _x
        self.y = _y
        self.z = -_z

class HighBayControl:
    def __init__(self):
        self.switcher = {
            1: StoragePlace(3100, 1930),
            2: StoragePlace(5040, 1930),
            3: StoragePlace(3100, 1000),
            4: StoragePlace(5040, 1000),
            5: StoragePlace(3100, 80),
            6: StoragePlace(5040, 80)
        }

        self.position = Position(0, 0, 0)

        import ftrobopy
        txt_con = ftrobopy.ftrobopy(host='10.0.0.12', port=65000)

        self.M1 = txt_con.motor(1)
        self.M2 = txt_con.motor(2)
        self.M3 = txt_con.motor(3)
        self.I1 = txt_con.input(1)
        self.I2 = txt_con.input(2)
        self.I3 = txt_con.input(3)
        self.I4 = txt_con.input(4)

    def arm_back_init(self):
        self.M1.setSpeed(512)
        while self.I1.state() == 0:
            continue
        else:
            self.M1.stop()
        self.position.x = 0

    def arm_down_init(self):
        self.M3.setSpeed(512)
        while self.I3.state() == 0:
            continue
        else:
            self.M3.stop()
        self.position.y = 0

    def arm_forth_till(self, x):
        self.M1.setDistance(x)
        self.M1.setSpeed(-512)

        while not self.M1.finished():
            continue
        else:
            self.M1.stop()

        self.position.x = self.M1.getCurrentDistance()

    def arm_up_till(self, y):
        self.M3.setDistance(y)
        self.M3.setSpeed(-512)

        while not self.M3.finished():
            continue
        else:
            self.M3.stop()

        self.position.y = self.M3.getCurrentDistance()

    def arm_down_till(self, y):
        self.M3.setDistance(y)
        self.M3.setSpeed(512)

        while not self.M3.finished():
            continue
        else:
            self.M3.stop()

        self.position.y = self.M3.getCurrentDistance()

    def fork_back(self):
        self.M2.setSpeed(512)

        while self.I2.state() == 0:
            continue
        else:
            self.M2.stop()

        self.position.z = 0

    def fork_forth(self):
        self.M2.setSpeed(-512)

        while self.I4.state() == 0:
            continue
        else:
            self.M2.stop()

        self.position.z = 100

    def initialize(self):
        self.fork_back()
        self.arm_down_init()
        self.arm_back_init()

    def pick_up_item(self):
        self.fork_forth()
        self.arm_up_till(30) # for Testing purpose: Default value is 80
        self.fork_back()
        self.arm_down_init()

    def put_down_item(self):
        self.fork_forth()
        self.arm_down_till(60) # for Testing purpose: Default value is 80
        self.fork_back()

    def move_fork(self, x, y):  # switchcase
        self.arm_forth_till(x)
        self.arm_up_till(y)