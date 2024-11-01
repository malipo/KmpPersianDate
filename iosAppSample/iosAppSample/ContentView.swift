import SwiftUI
import kmp_persian_datetime

struct ContentView: View {
    
    private let persianDate = PersianDate()

	var body: some View {
        VStack(alignment: .leading){
            HStack{
                Text("Persian date: ")
                Text(persianDate.getFullDatetimeWithMonthName(date: "2025-03-21T18:01:41Z"))
            }
            
            HStack{
                Text("Persian date: ")
                Text(persianDate.getFullDatetimeWithMonthNumber(date: "2025-03-21T18:01:41Z"))
            }
            
            HStack{
                Text("Persian date: ")
                Text(persianDate.daysAgo(date: "2024-10-26T12:01:41Z"))
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
