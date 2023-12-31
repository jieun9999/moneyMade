import styled from "styled-components";
// import axios from "axios";
import { useState, useRef, useEffect } from "react";

// import DeleteIcon from "../../../assets/delete.svg";
import YellowLeft from "../../../assets/yellowleft.svg";
import YellowRight from "../../../assets/yellowright.svg";
import Stockimg from "../../../assets/svg/stock.svg";

import { Account, ApiResponse } from "../../../interface/asset";

// interface Item {
//   id: number;
//   stock_name: string;
//   stock_amount: number;
// }

interface SavingAccountProps {
  assetdata?: ApiResponse["data"];
}

const Main = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

const StockList = styled.div`
  display: flex;
  justify-content: center;
`;

const StockContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 8vh;
  width: 8vw;
  border-radius: 3rem;
  box-shadow: 0px 4px 13px 0px rgb(0, 0, 0, 0.1);
  border: 1px solid #d9d9d9;
  padding: 3rem;
  margin: 5rem;
  position: relative;
`;

const Top = styled.div`
  display: flex;
  align-items: center;
`;

const StockName = styled.div`
  font-size: 3.5rem;
  margin: 2rem;
  color: #414141;
`;

// const Delete = styled.div`
//   cursor: pointer;
//   width: 3rem;
//   height: 3rem;
//   background-image: url(${DeleteIcon});
//   background-size: cover;
//   background-repeat: no-repeat;
//   margin-left: 10rem;

//   position: absolute;
//   top: 2rem;
//   right: 2rem;
// `;

const StockAmount = styled.div`
  font-size: 3.5rem;
`;

const EmptyText = styled.div`
  font-size: 3rem;
  text-align: center;
  margin-top: 5rem;
  margin-bottom: 5rem;
`;

const PageButton = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const LeftButton = styled.img`
  cursor: pointer;
  margin-right: 5rem;
`;

const RightButton = styled.img`
  cursor: pointer;
  margin-left: 5rem;
`;

const StockImg = styled.div`
  background: url(${Stockimg});
  background-size: 100% 100%;
  background-position: center;

  /* margin-right: 2rem; */

  width: 6rem;
  height: 6rem;
`;

export default function Stock({ assetdata }: SavingAccountProps) {
  // const [data, setData] = useState<Item[]>([]);
  const [displayedData, setDisplayedData] = useState<Account[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [stockFilter, setStockFilter] = useState<Account[]>([]); // stockFilter 상태 추가
  const StockBoxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (assetdata){
      const accountsList = assetdata?.monthlyResponseDto.accountsList;
      const filter = accountsList?.filter((e) => {
        return e.acoountType === "증권";
      });
      if (filter && filter.length > 0) {
        setDisplayedData(filter.slice(currentIndex, currentIndex + 3));
        setStockFilter(filter)
      }
    }
  }, [assetdata, currentIndex]);

  

  const handlePrevious = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 3);
    }
  };

  const handleNext = () => {
    if (assetdata && stockFilter) {
      const totalCount = stockFilter.length;
      const nextIndex = currentIndex + 3;
      if (nextIndex < totalCount) {
        setCurrentIndex(nextIndex);
      }
  }
}

  return (
    <Main ref={StockBoxRef}>
      {(displayedData?.length ?? 0) > 0 ? (
        <StockList>
          {displayedData?.map((el) => (
            <StockContainer key={el.accountId}>
              {/* <Delete onClick={() => handleDelete(el.accountId)} /> */}
              <Top>
                <StockImg />
                <StockName>{el.bankname}</StockName>
              </Top>
              <StockAmount>{el.balance.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")}원</StockAmount>
            </StockContainer>
          ))}
        </StockList>
      ) : (
        <EmptyText>표시할 내용이 없습니다.</EmptyText>
      )}
      <PageButton>
        <LeftButton src={YellowLeft} alt="Left" onClick={handlePrevious} />
        <RightButton src={YellowRight} alt="Right" onClick={handleNext} />
      </PageButton>
    </Main>
  );
}


// useEffect(() => {
  //   getData();
  // }, []);

  // const getData = async () => {
  //   try {
  //     const response = await axios.get("http://localhost:3000/stock");
  //     const data = response.data;
  //     setData(data);
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };

  // const handleDelete = async (id: number) => {
  //   try {
  //     await axios.delete(`http://localhost:3000/stock/${id}`);
  //     // getData();
  //   } catch (error) {
  //     console.log(error);
  //   }