// IAidlInterface.aidl
package com.inso.watch.server;

// Declare any non-default types here with import statements
import com.inso.watch.server.Person;

interface IRemoteAidlInterface {
    void show(in Person p);
    String getPersonUserName();
    String getPersonUserAge();
    int add(int a,int b);
}
