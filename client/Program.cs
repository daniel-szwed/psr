using System;
using System.Net.Http;
using System.Linq;
using System.Text;
using System.Text.Json;

namespace RestClient
{
    class Program
    {
        static void Main(string[] args)
        {
            while (true) {
                Console.WriteLine("Podaj liczby oddzielone srednikami:");

                var input = Console.ReadLine();

                var numbers = Array.ConvertAll(input.Trim().Split(';', options: StringSplitOptions.RemoveEmptyEntries), Double.Parse);
    
                var sorted = string.Join("; ", Sort(numbers).Select(numbers => numbers.ToString()).ToArray());

                Console.WriteLine($"Wynik: {sorted}");
            }
        }

        static double[] Sort(double[] numbers) {
            using var client = new HttpClient();
            var serialized = JsonSerializer.Serialize(numbers);
            var conent = new StringContent(serialized, Encoding.UTF8, "application/json");
            var response = client.PostAsync("http://localhost:8080/sort", conent).GetAwaiter().GetResult();
            var responseString = response.Content.ReadAsStringAsync().GetAwaiter().GetResult();
            
            return JsonSerializer.Deserialize<double[]>(responseString);
        }
    }
}
