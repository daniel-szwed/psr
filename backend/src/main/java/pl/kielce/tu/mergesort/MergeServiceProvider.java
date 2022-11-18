package pl.kielce.tu.mergesort;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Objects;

class MergeService {
    private String address;
    private int failureCounter;

    MergeService(String address) {
        this.address = address;
        this.failureCounter = 0;
    }

    public String getAddress() {
        return address;
    }

    public int getFailureCounter() {
        return failureCounter;
    }

    public void resetFailureCounter() {
        failureCounter = 0;
    }

    public void failureOccurs() {
        failureCounter += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MergeService that = (MergeService) o;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}

@Service
@Scope("singleton")
public class MergeServiceProvider {
    private CircuralList services;

    public MergeServiceProvider() {
        this.services = new CircularLinkedList();
    }

    public void addNode(String address) {
        synchronized (this) {
            if (services.contains(address)) {
                MergeService ms = (MergeService) services.getActualElementData();
                ms.resetFailureCounter();
            } else {
                MergeService ms = new MergeService(address);
                if(services.isEmpty())
                    services.insertFirst(ms);
                else
                    services.insertAfterActual(ms);
            }
        }
    }

    public void removeNode(String address) {
        synchronized (this) {
            services.delete(new MergeService(address));
        }
    }

    public String getNextNodeAddress() throws Exception {
        synchronized (this) {
            boolean hasNext = services.goToNextElement();
            if (!hasNext)
                throw new Exception("Services are unavailable");
            return ((MergeService) services.getActualElementData()).getAddress();
        }
    }

    public String failureOccurs(String address) throws Exception {
        synchronized (this) {
            if (services.contains(new MergeService(address))) {
                MergeService ms = (MergeService) services.getActualElementData();
                if (ms.getFailureCounter() > 3) {
                    services.deleteActualElement();
                    return getNextNodeAddress();
                } else {
                    ms.failureOccurs();
                    return ms.getAddress();
                }
            } else {
                return getNextNodeAddress();
            }
        }
    }
}
