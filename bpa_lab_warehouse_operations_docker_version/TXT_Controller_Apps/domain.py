import os

class HighbayDomain:
    def __init__(self):
        from control import HighBayControl
        self.control = HighBayControl()

    def pick_product(self):
        try:
            self.control.initialize()
            self.control.arm_up_till(60) # for Testing purpose: Default value is 50
            self.control.pick_up_item()
        finally:
            return True

    def put_product(self, place):
        try:
            # Move product to location
            _x = self.control.switcher.get(place).x
            _y = self.control.switcher.get(place).y
            self.control.move_fork(_x, _y)

            self.control.arm_up_till(30) # for Testing purpose: Default value is 50
            self.control.put_down_item()

            self.control.initialize()
        except Exception as error:
            print(error)
        finally:   
            return True

    def get_product(self, place):
        try:
            self.control.initialize()

            # Move product to location
            _x = self.control.switcher.get(place).x
            _y = self.control.switcher.get(place).y
            self.control.move_fork(_x, _y)

            self.control.pick_up_item()
        except Exception as error:
            print(error)
        finally:
            return True

    def place_product(self):   
        try:     
            self.control.initialize()

            self.control.arm_up_till(90) # for Testing purpose: Default value is 50
            self.control.put_down_item()

            self.control.initialize()
        finally:    
            return True