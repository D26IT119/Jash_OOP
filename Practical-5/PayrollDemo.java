abstract class Employee {
    String name;
    int id;

    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }
    abstract double monthlySalary();
}

class FullTime extends Employee {
    double salary;

    FullTime(String name, int id, double salary) {
        super(name, id);
        this.salary = salary;
    }
    double monthlySalary() {
        return salary;
    }
}

class PartTime extends Employee {
    double hours;
    double rate;

    PartTime(String name, int id, double hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }
    double monthlySalary() {
        return hours * rate;
    }
}

class Intern extends Employee {
    double stipend;

    Intern(String name, int id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }
    double monthlySalary() {
        return stipend;
    }
}

public class PayrollDemo {
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTime("Kriti", 101, 7000),
            new PartTime("Isha", 102, 60, 30),
            new Intern("Ravi", 103, 2000),
            new FullTime("Dhyey", 104, 5000)
        };

        double total = 0;

        for (Employee employee : employees) {
            double salary = employee.monthlySalary();
            System.out.println(employee.name + " (ID: " + employee.id + "): Salary = " + salary);
            if (employee instanceof Intern) {
                System.out.println("This employee is an intern");
            }
            total += salary;
        }
        System.out.println("Total payroll = " + total);
    }
}

