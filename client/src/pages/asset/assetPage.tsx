import * as S from "../../pages/asset/assetPageStyled";
import AssetHeader from "../../components/card/Asset/AssetHeader";
import AssetProfileContainer from "../../containers/assetProfileContainer";
import SavingAccount from "../../components/card/Asset/SavingAccount";
import Stock from "../../components/card/Asset/Stock";
import Property from "../../components/card/Asset/PropertyList";
import Car from "../../components/card/Asset/CarList";
import Cash from "../../components/card/Asset/CashList";
import AddProperty from "../../components/card/Asset/AddProperty";
import AddCar from "../../components/card/Asset/AddCar";
import AddCash from "../../components/card/Asset/AddCash";
import EditCashContainer from "../../containers/EditCashContainer";

import { useEffect, useState } from "react";
import axios, { AxiosResponse } from "axios";
import { useDispatch, useSelector } from "react-redux";
import { getLocalstorage } from "../../util/localStorage";
import { addProfile } from "../../redux/profileSlice";
import { RootState } from "../../redux/store";

export default function AssetPage() {
  const [assetdata, setAssetdata] = useState();
  const refreshKey = useSelector((state: RootState) => {
    return state.refreshSlice.key;
  });
  const [cashModal, setCashModal] = useState(false);
  const [editing, setEditing] = useState(false);
  const [cashClose, setCashClose] = useState(false);

  const dispatch = useDispatch();

  const propertyResponse = useSelector((state: RootState) => {
    return state.proFile.profileData?.data.propertyResponse;
  });

  const propertyFilter = propertyResponse?.find((el) => {
    return el.propertyType === "현금";
  });
  // console.log(Boolean(propertyFilter));

  useEffect(() => {
    setCashClose(Boolean(propertyFilter));
  }, [propertyFilter]);

  useEffect(() => {
    const currentData = new Date();
    const currentMonth = currentData.getMonth() + 1;
    const userId = getLocalstorage("userId");
    const acessToken = getLocalstorage("acessToken");

    axios.defaults.headers.common["Authorization"] = acessToken;
    axios
      .get(`/asset/myInfo/${userId}/${currentMonth}`, {
        headers: {
          "ngrok-skip-browser-warning": true,
        },
      })
      .then((res) => {
        // console.log(res.data);
        dispatch(addProfile(res.data));
        setAssetdata(res.data.data);
      })
      .catch((err) => {
        if (err.response) {
          const errMessage = (err.response as AxiosResponse<{ message: string }>)?.data.message;
          window.alert(errMessage);
          console.log(errMessage);
        } else {
          console.error(err);
          window.alert("알 수없는 오류가 발생했습니다.");
        }
      });
  }, [dispatch, refreshKey]);

  const closeModal = () => {
    setEditing(false); // 모달 상태를 false로 업데이트하여 닫음
  };
  const [propertyType] = useState("");

  return (
    <>
      <AssetHeader />
      <S.AssetPageContainer>
        <S.ContentContainer>
          <AssetProfileContainer />
          <S.Grid>
            <S.Title>입출금 계좌</S.Title>
            <SavingAccount assetdata={assetdata} />
            <S.Title>증권 계좌</S.Title>
            <Stock assetdata={assetdata} />
            {assetdata && <S.Title>부동산</S.Title>}
            {assetdata && <Property assetdata={assetdata} />}
            {assetdata && <S.Title>자동차</S.Title>}
            {assetdata && <Car assetdata={assetdata} />}
            {assetdata && (
              <S.Title>
                현금{}
                <S.RetouchButton
                  onClick={() => {
                    setEditing(true);
                  }}
                >
                  <S.Retouchicon />
                  수정
                </S.RetouchButton>
              </S.Title>
            )}
            {assetdata && <Cash assetdata={assetdata} />}
            <S.AddButtons>
              <AddProperty />
              <AddCar />
              {!cashClose && <AddCash cashModal={cashModal} setCashModal={setCashModal} editing={editing} setEditing={setEditing} />}
            </S.AddButtons>
            {editing && <EditCashContainer closeModal={closeModal} propertyType={propertyType} />}
          </S.Grid>
        </S.ContentContainer>
      </S.AssetPageContainer>
    </>
  );
}
