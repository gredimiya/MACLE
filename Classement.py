
class Classement():

    def __init__(self, nb_place):
        self.top=["" for i in range(nb_place)]
        self.nb_place=nb_place
        self.out=[]
    
    def add_top(self, name, position):
        if self.top[position-1]=="":
            self.top[position-1]=name
        else:
            self.decaler(position-1)
            self.top[position-1]=name

    def add_out(self,name):
        self.out.append(name)
        
    def decaler(self, position):
        self.out.append(self.top[self.nb_place-1])
        for i in range(self.nb_place-2,position-1,-1):
            self.top[i+1]=self.top[i]
    
    def supp_out(self,name):
        self.out.remove(name)
